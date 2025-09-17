package saviing.bank.transaction.application.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import saviing.bank.account.api.AccountInternalApi;
import saviing.bank.account.api.request.DepositAccountRequest;
import saviing.bank.account.api.request.GetAccountRequest;
import saviing.bank.account.api.request.WithdrawAccountRequest;
import saviing.bank.account.api.response.AccountApiResponse;
import saviing.bank.account.api.response.AccountInfoResponse;
import saviing.bank.account.api.response.BalanceUpdateResponse;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.application.event.TransferFailedEvent;
import saviing.bank.transaction.application.event.TransferSettledEvent;
import saviing.bank.transaction.application.port.in.TransferUseCase;
import saviing.bank.transaction.application.port.in.command.TransferCommand;
import saviing.bank.transaction.application.port.in.result.TransferResult;
import saviing.bank.transaction.application.port.out.LoadTransactionPort;
import saviing.bank.transaction.application.port.out.SaveTransactionPort;
import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.TransactionType;
import saviing.bank.transaction.domain.model.TransferStatus;
import saviing.bank.transaction.domain.model.account.AccountSnapshot;
import saviing.bank.transaction.domain.model.account.AccountStatusSnapshot;
import saviing.bank.transaction.domain.model.ledger.LedgerPairSnapshot;
import saviing.bank.transaction.domain.service.LedgerService;
import saviing.bank.transaction.domain.service.TransferDomainService;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.domain.vo.TransferId;
import saviing.bank.transaction.exception.AccountApiCallException;
import saviing.bank.transaction.exception.TransferInProgressException;
import saviing.bank.transaction.exception.TransactionNotFoundException;
import saviing.common.annotation.ExecutionTime;

/**
 * 헥사고날 아키텍처 기준으로 송금 유즈케이스를 오케스트레이션하는 애플리케이션 서비스.
 * Ledger 상태 전환, Account BC 내부 API 호출, Transaction 저장, 도메인 이벤트 발행을 한 곳에서 조율한다.
 */
@Slf4j
@ExecutionTime
@Service
@RequiredArgsConstructor
public class TransferService implements TransferUseCase {

    private final LedgerService ledgerService;
    private final TransferDomainService transferDomainService;
    private final SaveTransactionPort saveTransactionPort;
    private final LoadTransactionPort loadTransactionPort;
    private final AccountInternalApi accountInternalApi;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 송금 명령을 처리한다. 멱등성을 확인하고 Ledger/Transaction/Account API를 순차적으로 실행한다.
     *
     * @param command 송금 요청 정보
     * @return 송금 처리 결과
     */
    @Override
    @Transactional
    public TransferResult transfer(TransferCommand command) {
        TransferId transferId = resolveTransferId(command.idempotencyKey());
        log.info("송금 시작: transferId={}, sourceAccountId={}, targetAccountId={}, amount={}",
            transferId.value(), command.sourceAccountId(), command.targetAccountId(), command.amount().amount());
        LedgerPairSnapshot ledgerSnapshot = ledgerService.initializeTransfer(
            transferId,
            command.sourceAccountId(),
            command.targetAccountId(),
            command.amount(),
            command.valueDate(),
            command.transferType(),
            command.idempotencyKey()
        );

        transferDomainService.ensureIdempotency(transferId, command.idempotencyKey());

        // 멱등 재호출로 이미 완료/실패한 송금이면 추가 처리 없이 결과만 반환한다.
        if (ledgerSnapshot.status().isTerminal()) {
            log.info("멱등 재호출로 기존 결과 반환: transferId={}, status={}", transferId.value(), ledgerSnapshot.status());
            return mapToResult(ledgerSnapshot);
        }

        if (ledgerSnapshot.status() != TransferStatus.REQUESTED) {
            throw new TransferInProgressException(
                Map.of(
                    "transferId", transferId.value(),
                    "status", ledgerSnapshot.status().name()
                )
            );
        }

        // Account BC의 도메인 엔티티 대신 응답 스냅샷만 사용해 경계를 분리한다.
        AccountSnapshot sourceAccount = loadAccount(command.sourceAccountId());
        AccountSnapshot targetAccount = loadAccount(command.targetAccountId());

        transferDomainService.validatePreconditions(
            sourceAccount,
            targetAccount,
            command.amount(),
            command.valueDate(),
            command.transferType()
        );

        Instant startedAt = Instant.now();
        TransactionId debitTransactionId = null;
        TransactionId creditTransactionId = null;
        LedgerPairSnapshot currentSnapshot = ledgerSnapshot;
        boolean withdrawCompleted = false;

        try {
            // 출금/입금은 AccountInternalApi를 통해 처리하며 실패 시 도메인 예외로 변환한다.
            withdraw(command.sourceAccountId(), command.amount());
            withdrawCompleted = true;
            debitTransactionId = createTransaction(
                command.sourceAccountId(),
                TransactionType.TRANSFER_OUT,
                TransactionDirection.DEBIT,
                command.amount(),
                command.valueDate(),
                command.memo(),
                startedAt
            );
            currentSnapshot = ledgerService.markEntryPosted(
                transferId,
                TransactionDirection.DEBIT,
                debitTransactionId,
                startedAt
            );

            deposit(command.targetAccountId(), command.amount());
            creditTransactionId = createTransaction(
                command.targetAccountId(),
                TransactionType.TRANSFER_IN,
                TransactionDirection.CREDIT,
                command.amount(),
                command.valueDate(),
                command.memo(),
                Instant.now()
            );
            currentSnapshot = ledgerService.markEntryPosted(
                transferId,
                TransactionDirection.CREDIT,
                creditTransactionId,
                Instant.now()
            );

            currentSnapshot = ledgerService.markTransferSettled(transferId, Instant.now());
            linkTransactions(debitTransactionId, creditTransactionId);
            transferDomainService.onTransferSettled(transferId, currentSnapshot);
            publishSettledEvent(currentSnapshot, debitTransactionId, creditTransactionId);
            log.info("송금 완료: transferId={}, debitTxId={}, creditTxId={}, status={}",
                transferId.value(),
                debitTransactionId != null ? debitTransactionId.value() : null,
                creditTransactionId != null ? creditTransactionId.value() : null,
                currentSnapshot.status());
            return mapToResult(currentSnapshot);
        } catch (RuntimeException ex) {
            String failureReason = ex.getMessage();
            if (withdrawCompleted) {
                failureReason += attemptDebitCompensation(command, transferId);
            }
            // 예외 발생 시 Ledger 상태를 FAILED로 전환하고 도메인 후처리를 실행한다.
            LedgerPairSnapshot failedSnapshot = ledgerService.markTransferFailed(transferId, failureReason);
            transferDomainService.onTransferFailed(transferId, failedSnapshot, ex);
            publishFailedEvent(transferId, failedSnapshot, ex);
            log.warn("송금 실패: transferId={}, status={}, reason={}", transferId.value(),
                failedSnapshot.status(), failedSnapshot.failureReason(), ex);
            throw ex;
        }
    }

    /**
     * AccountInternalApi를 호출해 계좌 정보를 스냅샷 형태로 조회한다.
     */
    private AccountSnapshot loadAccount(Long accountId) {
        AccountApiResponse<AccountInfoResponse> response = accountInternalApi.getAccount(GetAccountRequest.of(accountId));
        if (response instanceof AccountApiResponse.Success<AccountInfoResponse> success) {
            AccountInfoResponse data = success.data();
            return new AccountSnapshot(
                data.accountId(),
                MoneyWon.of(data.balance()),
                AccountStatusSnapshot.from(data.status())
            );
        }
        throw new AccountApiCallException(
            "계좌 조회에 실패했습니다",
            Map.of("accountId", accountId)
        );
    }

    /**
     * AccountInternalApi를 통해 출금을 수행하고 잔액 변경 결과를 반환한다.
     */
    private BalanceUpdateResponse withdraw(Long accountId, MoneyWon amount) {
        AccountApiResponse<BalanceUpdateResponse> response = accountInternalApi.withdraw(
            WithdrawAccountRequest.of(accountId, amount.amount())
        );
        if (response instanceof AccountApiResponse.Success<BalanceUpdateResponse> success) {
            return success.data();
        }
        throw new AccountApiCallException(
            "계좌 출금에 실패했습니다",
            Map.of("accountId", accountId)
        );
    }

    /**
     * AccountInternalApi를 통해 입금을 수행하고 잔액 변경 결과를 반환한다.
     */
    private BalanceUpdateResponse deposit(Long accountId, MoneyWon amount) {
        AccountApiResponse<BalanceUpdateResponse> response = accountInternalApi.deposit(
            DepositAccountRequest.of(accountId, amount.amount())
        );
        if (response instanceof AccountApiResponse.Success<BalanceUpdateResponse> success) {
            return success.data();
        }
        throw new AccountApiCallException(
            "계좌 입금에 실패했습니다",
            Map.of("accountId", accountId)
        );
    }

    /**
     * 송금 과정에서 생성되는 거래 엔트리를 저장한다.
     */
    private TransactionId createTransaction(
        Long accountId,
        TransactionType transactionType,
        TransactionDirection direction,
        MoneyWon amount,
        LocalDate valueDate,
        String description,
        Instant postedAt
    ) {
        Transaction transaction = Transaction.create(
            accountId,
            transactionType,
            direction,
            amount,
            valueDate,
            postedAt,
            description
        );
        TransactionId transactionId = saveTransactionPort.saveTransaction(transaction);
        return transactionId;
    }

    /**
     * 출금 거래와 입금 거래를 서로 연관시키도록 업데이트한다.
     */
    private void linkTransactions(TransactionId debitTransactionId, TransactionId creditTransactionId) {
        Transaction debit = loadTransactionPort.loadTransaction(debitTransactionId)
            .orElseThrow(() -> new TransactionNotFoundException(
                Map.of("transactionId", debitTransactionId.value())
            ));
        Transaction credit = loadTransactionPort.loadTransaction(creditTransactionId)
            .orElseThrow(() -> new TransactionNotFoundException(
                Map.of("transactionId", creditTransactionId.value())
            ));
        debit.setRelatedTransaction(creditTransactionId);
        credit.setRelatedTransaction(debitTransactionId);
        saveTransactionPort.updateTransaction(debit);
        saveTransactionPort.updateTransaction(credit);
    }

    /**
     * 송금 완료 이벤트를 발행한다.
     */
    private void publishSettledEvent(
        LedgerPairSnapshot snapshot,
        TransactionId debitTransactionId,
        TransactionId creditTransactionId
    ) {
        eventPublisher.publishEvent(new TransferSettledEvent(
            snapshot.transferId(),
            debitTransactionId,
            creditTransactionId,
            snapshot.debitEntry() != null ? snapshot.debitEntry().amount() : MoneyWon.zero(),
            snapshot.transferType(),
            snapshot.updatedAt()
        ));
    }

    /**
     * 송금 실패 이벤트를 발행한다.
     */
    private void publishFailedEvent(
        TransferId transferId,
        LedgerPairSnapshot snapshot,
        Throwable cause
    ) {
        eventPublisher.publishEvent(new TransferFailedEvent(
            transferId,
            snapshot.status(),
            snapshot.failureReason(),
            cause,
            snapshot.updatedAt()
        ));
    }

    /**
     * Ledger 스냅샷을 REST 응답용 결과 객체로 변환한다.
     */
    private TransferResult mapToResult(LedgerPairSnapshot snapshot) {
        TransactionId debitId = snapshot.debitEntry() != null ? snapshot.debitEntry().transactionId() : null;
        TransactionId creditId = snapshot.creditEntry() != null ? snapshot.creditEntry().transactionId() : null;
        return TransferResult.builder()
            .transferId(snapshot.transferId())
            .debitTransactionId(debitId)
            .creditTransactionId(creditId)
            .status(snapshot.status())
            .completedAt(snapshot.updatedAt())
            .build();
    }

    /**
     * 멱등 키 기반으로 TransferId를 생성하거나 새로 발급한다.
     */
    private TransferId resolveTransferId(IdempotencyKey idempotencyKey) {
        if (idempotencyKey != null) {
            return TransferId.of(idempotencyKey.value());
        }
        return TransferId.newId();
    }

    /**
     * 실제 출금이 실행되었는지 확인한다.
     * DEBIT_POSTED 상태뿐만 아니라 출금 Transaction이 생성된 모든 경우를 포함한다.
     */
    private boolean isDebitExecuted(LedgerPairSnapshot snapshot, TransactionId debitTransactionId) {
        // 출금 Transaction이 생성되었다면 실제 계좌에서 출금이 완료된 상태
        if (debitTransactionId != null) {
            return true;
        }

        // Ledger 상태가 DEBIT_POSTED인 경우
        if (snapshot.status() == TransferStatus.DEBIT_POSTED) {
            return true;
        }

        // debitEntry에 transactionId가 있는 경우 (상태와 무관하게 Transaction이 생성됨)
        return snapshot.debitEntry() != null &&
               snapshot.debitEntry().transactionId() != null;
    }

    /**
     * 출금 성공 후 실패가 발생한 경우 원복을 시도한다.
     */
    private String attemptDebitCompensation(TransferCommand command, TransferId transferId) {
        try {
            log.warn("[COMPENSATION-START] 송금 보상 처리 시작 - transferId: {}, accountId: {}, amount: {}",
                transferId.value(), command.sourceAccountId(), command.amount().amount());

            deposit(command.sourceAccountId(), command.amount());

            TransactionId compensationTxId = createTransaction(
                command.sourceAccountId(),
                TransactionType.REVERSAL,
                TransactionDirection.CREDIT,
                command.amount(),
                command.valueDate(),
                "Transfer compensation for " + transferId.value(),
                Instant.now()
            );

            log.warn("[COMPENSATION-SUCCESS] 송금 보상 처리 완료 - transferId: {}, compensationTxId: {}",
                transferId.value(), compensationTxId.value());

            return "; compensationSuccess=" + compensationTxId.value();
        } catch (RuntimeException compensationEx) {
            log.warn("[COMPENSATION-FAILED] 송금 보상 처리 실패 - transferId: {}, error: {}",
                transferId.value(), compensationEx.getMessage(), compensationEx);
            return "; compensationFailed=" + compensationEx.getMessage();
        }
    }
}
