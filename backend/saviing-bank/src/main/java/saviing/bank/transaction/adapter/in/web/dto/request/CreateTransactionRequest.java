package saviing.bank.transaction.adapter.in.web.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Builder;

import saviing.bank.transaction.application.port.in.command.CreateTransactionWithAccountNumberCommand;

@Builder
public record CreateTransactionRequest(
    @NotNull(message = "계좌번호는 필수입니다")
    String accountNumber,

    @NotNull(message = "거래 유형은 필수입니다")
    String transactionType,

    @NotNull(message = "거래 방향은 필수입니다")
    String direction,

    @NotNull(message = "거래 금액은 필수입니다")
    @Positive(message = "거래 금액은 양수여야 합니다")
    BigDecimal amount,

    @NotNull(message = "가치일은 필수입니다")
    LocalDate valueDate,

    String idempotencyKey,
    String description
) {

    /**
     * Request를 CreateTransactionWithAccountNumberCommand로 변환하는 팩토리 메서드
     *
     * @return CreateTransactionWithAccountNumberCommand 인스턴스
     */
    public CreateTransactionWithAccountNumberCommand toCommand() {
        return CreateTransactionWithAccountNumberCommand.of(
            accountNumber,
            transactionType,
            direction,
            amount,
            valueDate,
            idempotencyKey,
            description
        );
    }
}