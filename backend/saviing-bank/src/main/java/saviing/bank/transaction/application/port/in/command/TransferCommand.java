package saviing.bank.transaction.application.port.in.command;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Builder;
import lombok.NonNull;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.TransferType;
import saviing.bank.transaction.domain.vo.IdempotencyKey;

/**
 * 송금 요청 정보를 담는 애플리케이션 계층 커맨드.
 * 계좌 ID, 금액, 가치일, 멱등 키 등 송금 처리에 필요한 모든 파라미터를 포함한다.
 */
@Builder
public record TransferCommand(
    @NonNull Long sourceAccountId,
    @NonNull Long targetAccountId,
    @NonNull MoneyWon amount,
    @NonNull LocalDate valueDate,
    @NonNull TransferType transferType,
    String memo,
    IdempotencyKey idempotencyKey,
    Instant requestedAt
) {

    /**
     * 내부 송금인 경우 출금/입금 계좌가 동일하지 않은지 검증한다.
     */
    public TransferCommand {
        if (sourceAccountId.equals(targetAccountId) && transferType == TransferType.INTERNAL) {
            throw new IllegalArgumentException("Source and target account must differ for internal transfer");
        }
    }
}
