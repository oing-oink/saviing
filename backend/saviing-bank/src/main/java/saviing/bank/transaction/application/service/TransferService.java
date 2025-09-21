package saviing.bank.transaction.application.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

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
import saviing.bank.transaction.application.port.in.TransferUseCase;
import saviing.bank.transaction.application.port.in.command.TransferCommand;
import saviing.bank.transaction.application.port.in.result.TransferResult;
import saviing.bank.transaction.application.port.out.LoadCustomerNamePort;
import saviing.bank.transaction.application.port.out.LoadTransactionPort;
import saviing.bank.transaction.application.port.out.SaveTransactionPort;
import saviing.bank.transaction.domain.model.Transaction;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.domain.model.TransactionType;
import saviing.bank.transaction.domain.model.transfer.TransferStatus;
import saviing.bank.transaction.domain.vo.AccountSnapshot;
import saviing.bank.transaction.domain.vo.AccountStatusSnapshot;
import saviing.bank.transaction.domain.vo.TransferSnapshot;
import saviing.bank.transaction.domain.service.TransferDomainService;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.domain.vo.TransactionId;
import saviing.bank.transaction.exception.AccountApiCallException;
import saviing.bank.transaction.exception.DuplicateTransferRequestException;
import saviing.bank.transaction.exception.TransferInProgressException;
import saviing.bank.transaction.exception.TransferValidationException;
import saviing.bank.transaction.exception.TransactionException;
import saviing.bank.transaction.exception.TransactionNotFoundException;
import saviing.common.annotation.ExecutionTime;

/**
 * 헥사고날 아키텍처 기준으로 송금 유즈케이스를 오케스트레이션하는 애플리케이션 서비스.
 * Ledger 상태 전환, Account BC 내부 API 호출, Transaction 저장을 한 곳에서 조율한다.
 */
@Slf4j
@ExecutionTime
@Service
@RequiredArgsConstructor
public class TransferService implements TransferUseCase {

    private static final String GENERIC_FAILURE_MESSAGE = "내부 시스템 오류로 송금이 실패했습니다.";

    private final LedgerService ledgerService;
    private final TransferDomainService transferDomainService;
    private final SaveTransactionPort saveTransactionPort;
    private final LoadTransactionPort loadTransactionPort;
    private final LoadCustomerNamePort loadCustomerNamePort;
    private final AccountInternalApi accountInternalApi;

    /**
     * 송금 명령을 처리한다. 멱등성을 확인하고 Ledger/Transaction/Account API를 순차적으로 실행한다.
     *
     * @param command 송금 요청 정보
     * @return 송금 처리 결과
     */
    @Override
    @Transactional
    public TransferResult transfer(TransferCommand command) {
        IdempotencyKey idempotencyKey = command.idempotencyKey();
        if (idempotencyKey == null) {
            throw new TransferValidationException(
                "멱등성 키가 필요합니다",
                Map.of(
                    "sourceAccountId", command.sourceAccountId(),
                    "targetAccountId", command.targetAccountId(),
                    "valueDate", command.valueDate()
                )
            );
        }
        log.info("송금 시작: idempotencyKey={}, sourceAccountId={}, targetAccountId={}, amount={}",
            idempotencyKey.value(), command.sourceAccountId(), command.targetAccountId(), command.amount().amount());
        TransferSnapshot ledgerSnapshot = ledgerService.initializeTransfer(
            idempotencyKey,
            command.sourceAccountId(),
            command.targetAccountId(),
            command.amount(),
            command.valueDate(),
            command.transferType()
        );

        transferDomainService.ensureIdempotency(idempotencyKey);

        // 멱등 재호출로 이미 완료/실패한 송금이면 예외를 발생시켜 중복 처리를 차단한다.
        if (ledgerSnapshot.status().isTerminal()) {
            log.info(
                "멱등 재호출 감지: idempotencyKey={}, status={}",
                idempotencyKey.value(),
                ledgerSnapshot.status()
            );
            throw new DuplicateTransferRequestException(
                Map.of(
                    "idempotencyKey", idempotencyKey.value(),
                    "status", ledgerSnapshot.status().name()
                )
            );
        }

        if (ledgerSnapshot.status() != TransferStatus.REQUESTED) {
            throw new TransferInProgressException(
                Map.of(
                    "idempotencyKey", idempotencyKey.value(),
                    "status", ledgerSnapshot.status().name()
                )
            );
        }

        // Account BC의 도메인 엔티티 대신 응답 스냅샷만 사용해 경계를 분리한다.
        AccountSnapshot sourceAccount = loadAccount(command.sourceAccountId());
        AccountSnapshot targetAccount = loadAccount(command.targetAccountId());

        // 고객 이름 미리 조회 (description 설정용)
        String recipientName = loadCustomerNamePort.loadCustomerName(targetAccount.customerId())
            .orElse("(알 수 없음)");
        String senderName = loadCustomerNamePort.loadCustomerName(sourceAccount.customerId())
            .orElse("(알 수 없음)");

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
        TransferSnapshot currentSnapshot = ledgerSnapshot;
        boolean withdrawCompleted = false;

        try {

            // 출금/입금은 AccountInternalApi를 통해 처리하며 실패 시 도메인 예외로 변환한다.
            BalanceUpdateResponse withdrawResponse = withdraw(command.sourceAccountId(), command.amount());
            withdrawCompleted = true;
            debitTransactionId = createTransaction(
                command.sourceAccountId(),
                TransactionType.TRANSFER_OUT,
                TransactionDirection.DEBIT,
                command.amount(),
                MoneyWon.of(withdrawResponse.currentBalance()),
                command.valueDate(),
                recipientName, // 출금 거래는 수취인 이름으로 설정
                startedAt
            );
            currentSnapshot = ledgerService.markEntryPosted(
                command.sourceAccountId(),
                idempotencyKey,
                TransactionDirection.DEBIT,
                debitTransactionId,
                startedAt
            );

            BalanceUpdateResponse depositResponse = deposit(command.targetAccountId(), command.amount());
            // 입금 거래는 memo > 송금자 이름 순으로 설정
            String depositDescription = (command.memo() != null && !command.memo().trim().isEmpty())
                ? command.memo()
                : senderName;
            creditTransactionId = createTransaction(
                command.targetAccountId(),
                TransactionType.TRANSFER_IN,
                TransactionDirection.CREDIT,
                command.amount(),
                MoneyWon.of(depositResponse.currentBalance()),
                command.valueDate(),
                depositDescription,
                Instant.now()
            );
            currentSnapshot = ledgerService.markEntryPosted(
                command.sourceAccountId(),
                idempotencyKey,
                TransactionDirection.CREDIT,
                creditTransactionId,
                Instant.now()
            );

            currentSnapshot = ledgerService.markTransferSettled(command.sourceAccountId(), idempotencyKey, Instant.now());
            linkTransactions(debitTransactionId, creditTransactionId);
            transferDomainService.onTransferSettled(idempotencyKey, currentSnapshot);
            log.info("송금 완료: idempotencyKey={}, debitTxId={}, creditTxId={}, status={}",
                idempotencyKey.value(),
                debitTransactionId != null ? debitTransactionId.value() : null,
                creditTransactionId != null ? creditTransactionId.value() : null,
                currentSnapshot.status());
            return mapToResult(currentSnapshot);
        } catch (TransactionException domainEx) {
            handleDomainFailure(command, idempotencyKey, withdrawCompleted, recipientName, domainEx);
            throw domainEx;
        } catch (RuntimeException systemEx) {
            handleSystemFailure(command, idempotencyKey, withdrawCompleted, recipientName, systemEx);
            throw systemEx;
        }
    }

    /**
     * AccountInternalApi를 호출해 계좌 정보를 스냅샷 형태로 조회한다.
     */
    private AccountSnapshot loadAccount(Long accountId) {
        AccountApiResponse<AccountInfoResponse> response = accountInternalApi.getAccount(GetAccountRequest.of(accountId));
        if (response instanceof AccountApiResponse.Success<AccountInfoResponse>(AccountInfoResponse data)) {
            return new AccountSnapshot(
                data.accountId(),
                data.customerId(),
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
        if (response instanceof AccountApiResponse.Success<BalanceUpdateResponse>(BalanceUpdateResponse data)) {
            return data;
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
        if (response instanceof AccountApiResponse.Success<BalanceUpdateResponse>(BalanceUpdateResponse data)) {
            return data;
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
        MoneyWon balanceAfter,
        LocalDate valueDate,
        String description,
        Instant postedAt
    ) {
        Transaction transaction = Transaction.create(
            accountId,
            transactionType,
            direction,
            amount,
            balanceAfter,
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
     * Ledger 스냅샷을 REST 응답용 결과 객체로 변환한다.
     */
    private TransferResult mapToResult(TransferSnapshot snapshot) {
        TransactionId debitId = snapshot.debitEntry() != null ? snapshot.debitEntry().transactionId() : null;
        TransactionId creditId = snapshot.creditEntry() != null ? snapshot.creditEntry().transactionId() : null;
        return TransferResult.builder()
            .idempotencyKey(snapshot.idempotencyKey())
            .sourceAccountId(snapshot.sourceAccountId())
            .targetAccountId(snapshot.targetAccountId())
            .amount(snapshot.amount())
            .valueDate(snapshot.valueDate())
            .debitTransactionId(debitId)
            .creditTransactionId(creditId)
            .requestedAt(snapshot.createdAt())
            .status(snapshot.status())
            .completedAt(snapshot.updatedAt())
            .failureReason(snapshot.failureReason())
            .build();
    }

    /**
     * 출금 성공 후 실패가 발생한 경우 원복을 시도한다.
     */
    private String attemptDebitCompensation(TransferCommand command, IdempotencyKey idempotencyKey, String recipientName) {
        try {
            log.warn("[COMPENSATION-START] 송금 보상 처리 시작 - idempotencyKey: {}, accountId: {}, amount: {}",
                idempotencyKey.value(), command.sourceAccountId(), command.amount().amount());

            BalanceUpdateResponse compensationResponse = deposit(command.sourceAccountId(), command.amount());

            TransactionId compensationTxId = createTransaction(
                command.sourceAccountId(),
                TransactionType.REVERSAL,
                TransactionDirection.CREDIT,
                command.amount(),
                MoneyWon.of(compensationResponse.currentBalance()),
                command.valueDate(),
                recipientName + " 송금취소",
                Instant.now()
            );

            log.warn("[COMPENSATION-SUCCESS] 송금 보상 처리 완료 - idempotencyKey: {}, compensationTxId: {}",
                idempotencyKey.value(), compensationTxId.value());

            return "; compensationStatus=SUCCESS; compensationTxId=" + compensationTxId.value();
        } catch (RuntimeException compensationEx) {
            log.warn("[COMPENSATION-FAILED] 송금 보상 처리 실패 - idempotencyKey: {}, error: {}",
                idempotencyKey.value(), compensationEx.getMessage(), compensationEx);
            return "; compensationStatus=FAILED";
        }
    }

    private void handleDomainFailure(
        TransferCommand command,
        IdempotencyKey idempotencyKey,
        boolean withdrawCompleted,
        String recipientName,
        TransactionException domainEx
    ) {
        String failureReason = domainEx.getMessage();
        if (withdrawCompleted) {
            failureReason += attemptDebitCompensation(command, idempotencyKey, recipientName);
        }
        TransferSnapshot failedSnapshot = ledgerService.markTransferFailed(command.sourceAccountId(), idempotencyKey, failureReason);
        transferDomainService.onTransferFailed(idempotencyKey, failedSnapshot, domainEx);
        log.warn("송금 실패(도메인): idempotencyKey={}, status={}, reason={}",
            idempotencyKey.value(), failedSnapshot.status(), failedSnapshot.failureReason(), domainEx);
    }

    private void handleSystemFailure(
        TransferCommand command,
        IdempotencyKey idempotencyKey,
        boolean withdrawCompleted,
        String recipientName,
        RuntimeException systemEx
    ) {
        String failureReason = GENERIC_FAILURE_MESSAGE;
        if (withdrawCompleted) {
            failureReason += attemptDebitCompensation(command, idempotencyKey, recipientName);
        }
        TransferSnapshot failedSnapshot = ledgerService.markTransferFailed(command.sourceAccountId(), idempotencyKey, failureReason);
        transferDomainService.onTransferFailed(idempotencyKey, failedSnapshot, systemEx);
        log.error("송금 실패(시스템): idempotencyKey={}, status={}, reason={}",
            idempotencyKey.value(), failedSnapshot.status(), failedSnapshot.failureReason(), systemEx);
    }

}
