package saviing.bank.account.adapter.in.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import saviing.bank.account.application.port.in.command.CreateAccountCommand;

public record CreateAccountRequest(
    @NotNull(message = "고객ID는 필수입니다")
    Long customerId,

    @NotNull(message = "상품유형은 필수입니다")
    Long productId,

    // 적금 전용 필드들 (Optional)
    @Positive(message = "목표금액은 양수여야 합니다")
    Long targetAmount,

    @Valid
    SavingsTermRequest termPeriod,

    String maturityWithdrawalAccount
) {

    public record SavingsTermRequest(
        @NotNull(message = "기간 값은 필수입니다")
        @Positive(message = "기간 값은 양수여야 합니다")
        Integer value,

        @NotNull(message = "기간 단위는 필수입니다")
        String unit
    ) {}

    public CreateAccountCommand toCommand() {
        return CreateAccountCommand.of(
            customerId,
            productId,
            targetAmount,
            termPeriod != null ? termPeriod.value() : null,
            termPeriod != null ? termPeriod.unit() : null,
            maturityWithdrawalAccount
        );
    }
}