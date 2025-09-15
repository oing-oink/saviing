package saviing.bank.account.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import saviing.bank.account.application.port.in.command.CreateAccountCommand;

@Schema(description = "계좌 생성 요청")
public record CreateAccountRequest(
    @Schema(description = "고객 ID", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "고객ID는 필수입니다")
    Long customerId,

    @Schema(description = "상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "상품유형은 필수입니다")
    Long productId,

    @Schema(description = "목표금액 (적금 계좌만 필수, 원 단위 정수)", example = "1000000")
    @Positive(message = "목표금액은 양수여야 합니다")
    Long targetAmount,

    @Schema(description = "적금 기간 (적금 계좌만 필수)")
    @Valid
    SavingsTermRequest termPeriod,

    @Schema(description = "만기 출금 계좌번호 (적금 계좌만, 선택사항)", example = "11012345678901234")
    String maturityWithdrawalAccount
) {

    @Schema(description = "적금 기간 정보")
    public record SavingsTermRequest(
        @Schema(description = "기간 값", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "기간 값은 필수입니다")
        @Positive(message = "기간 값은 양수여야 합니다")
        Integer value,

        @Schema(
            description = "기간 단위",
            example = "WEEKS",
            allowableValues = {"DAYS", "WEEKS", "MONTHS", "YEARS"},
            requiredMode = Schema.RequiredMode.REQUIRED
        )
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