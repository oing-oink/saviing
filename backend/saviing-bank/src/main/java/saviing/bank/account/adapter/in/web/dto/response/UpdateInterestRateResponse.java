package saviing.bank.account.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.math.RoundingMode;

import saviing.bank.account.application.port.in.result.UpdateInterestRateResult;

@Schema(description = "계좌 이자율 업데이트 응답", accessMode = Schema.AccessMode.READ_ONLY)
@Builder
public record UpdateInterestRateResponse(
    @Schema(description = "계좌 ID", example = "100001")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long accountId,

    @Schema(description = "현재 보너스 금리 (퍼센트 단위, 예: 3.50 = 3.5%)", example = "3.50")
    BigDecimal currentBonusRatePercentage
) {

    /**
     * UpdateInterestRateResult로부터 응답 DTO를 생성합니다.
     *
     * @param result 이자율 업데이트 결과
     * @return UpdateInterestRateResponse
     */
    public static UpdateInterestRateResponse from(UpdateInterestRateResult result) {
        return UpdateInterestRateResponse.builder()
            .accountId(result.accountId())
            .currentBonusRatePercentage(
                BigDecimal.valueOf(result.currentBonusRatePercentage())
                    .setScale(2, RoundingMode.HALF_UP)
            )
            .build();
    }

    /**
     * 계좌 ID와 보너스 금리로부터 응답 DTO를 생성합니다.
     *
     * @param accountId 계좌 ID
     * @param bonusRatePercentage 현재 보너스 금리 (백분율)
     * @return UpdateInterestRateResponse
     */
    public static UpdateInterestRateResponse of(Long accountId, Double bonusRatePercentage) {
        return UpdateInterestRateResponse.builder()
            .accountId(accountId)
            .currentBonusRatePercentage(
                BigDecimal.valueOf(bonusRatePercentage)
                    .setScale(2, RoundingMode.HALF_UP)
            )
            .build();
    }
}