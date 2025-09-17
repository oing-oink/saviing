package saviing.bank.transaction.domain.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.transfer.TransferType;
import saviing.bank.transaction.domain.vo.AccountSnapshot;
import saviing.bank.transaction.domain.vo.TransferSnapshot;
import saviing.bank.transaction.domain.vo.IdempotencyKey;
import saviing.bank.transaction.exception.InsufficientBalanceException;
import saviing.bank.transaction.exception.TransferValidationException;

/**
 * 송금 도메인 규칙을 실제로 구현한 기본 서비스.
 */
@Service
public class TransferDomainServiceImpl implements TransferDomainService {

    @Override
    /** {@inheritDoc} */
    public void validatePreconditions(
        AccountSnapshot sourceAccount,
        AccountSnapshot targetAccount,
        MoneyWon amount,
        LocalDate valueDate,
        TransferType transferType
    ) {
        if (amount == null || !amount.isPositive()) {
            throw new TransferValidationException(
                "송금 금액은 0보다 커야 합니다",
                Map.of("amount", amount != null ? amount.amount() : null)
            );
        }
        validateAccountActive(sourceAccount, "sourceAccountId");
        validateAccountActive(targetAccount, "targetAccountId");

        if (sourceAccount.balance().isLessThan(amount)) {
            throw new InsufficientBalanceException(
                Map.of(
                    "accountId", sourceAccount.accountId(),
                    "currentBalance", sourceAccount.balance().amount(),
                    "requestAmount", amount.amount()
                )
            );
        }

        validateValueDate(valueDate);
    }

    @Override
    /** {@inheritDoc} */
    public void ensureIdempotency(IdempotencyKey idempotencyKey) {
        // 멱등성 키 기반 중복 검증 로직을 추가할 수 있다.
    }

    @Override
    /** {@inheritDoc} */
    public void onTransferSettled(IdempotencyKey idempotencyKey, TransferSnapshot ledgerPair) {
        // 후속 처리(이벤트 발행 등)는 Application 계층에서 수행, 도메인에서는 체크만 수행
    }

    @Override
    /** {@inheritDoc} */
    public void onTransferFailed(IdempotencyKey idempotencyKey, TransferSnapshot ledgerPair, Throwable cause) {
        // 실패 시 추가 정합성 체크 또는 보상 로직 훅을 위해 남겨둔 메서드
    }

    private void validateAccountActive(AccountSnapshot account, String key) {
        if (!account.status().canTransact()) {
            throw new TransferValidationException(
                "거래가 허용되지 않은 계좌 상태입니다",
                Map.of(key, account.accountId(), "status", account.status().name())
            );
        }
    }

    private void validateValueDate(LocalDate valueDate) {
        LocalDate today = LocalDate.now();
        if (valueDate.isAfter(today.plusDays(1))) {
            throw new TransferValidationException(
                "가치일이 허용 범위를 초과했습니다",
                Map.of("valueDate", valueDate, "maxAllowedDate", today.plusDays(1))
            );
        }
        if (valueDate.isBefore(today.minusDays(30))) {
            throw new TransferValidationException(
                "가치일이 허용 범위보다 과거입니다",
                Map.of("valueDate", valueDate, "minAllowedDate", today.minusDays(30))
            );
        }
    }
}
