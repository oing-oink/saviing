package saviing.bank.account.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "계좌 이자율 업데이트 요청")
public record UpdateInterestRateRequest(
    @Schema(
        description = "새로 설정할 보너스 금리 (백분율)",
        example = "3.5",
        minimum = "0.0",
        maximum = "100.0",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "보너스 금리는 필수입니다")
    @DecimalMin(value = "0.0", message = "보너스 금리는 0% 이상이어야 합니다")
    @DecimalMax(value = "100.0", message = "보너스 금리는 100% 이하여야 합니다")
    BigDecimal newBonusRatePercentage
) {

    /**
     * Double로 변환된 금리를 반환합니다.
     *
     * @return 백분율 형태의 보너스 금리
     */
    public Double getBonusRateAsDouble() {
        return newBonusRatePercentage.doubleValue();
    }
}