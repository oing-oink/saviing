package saviing.bank.transaction.adapter.in.web.dto.request;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.application.port.in.command.TransferCommand;
import saviing.bank.transaction.domain.model.TransferType;
import saviing.bank.transaction.domain.vo.IdempotencyKey;

/**
 * REST 송금 요청 바디를 표현하는 DTO.
 */
public record TransferRequest(
    @NotNull @Positive Long sourceAccountId,
    @NotNull @Positive Long targetAccountId,
    @NotNull @Positive Long amount,
    @NotNull LocalDate valueDate,
    String memo,
    String idempotencyKey,
    TransferType transferType
) {

    /**
     * 컨트롤러에서 사용할 애플리케이션 커맨드로 변환한다.
     */
    public TransferCommand toCommand() {
        return TransferCommand.builder()
            .sourceAccountId(sourceAccountId)
            .targetAccountId(targetAccountId)
            .amount(MoneyWon.of(amount))
            .valueDate(valueDate)
            .memo(memo)
            .idempotencyKey(idempotencyKey != null ? IdempotencyKey.of(idempotencyKey) : null)
            .transferType(transferType != null ? transferType : TransferType.INTERNAL)
            .requestedAt(Instant.now())
            .build();
    }
}
