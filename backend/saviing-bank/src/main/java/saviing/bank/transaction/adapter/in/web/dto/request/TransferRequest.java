package saviing.bank.transaction.adapter.in.web.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import saviing.bank.transaction.application.port.in.command.TransferCommand;

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
    String transferType
) {

    /**
     * 컨트롤러에서 사용할 애플리케이션 커맨드로 변환한다.
     */
    public TransferCommand toCommand() {
        return TransferCommand.of(
            sourceAccountId,
            targetAccountId,
            amount,
            valueDate,
            transferType,
            memo,
            idempotencyKey
        );
    }
}
