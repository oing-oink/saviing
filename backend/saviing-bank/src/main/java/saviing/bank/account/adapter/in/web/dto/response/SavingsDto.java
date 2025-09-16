package saviing.bank.account.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

import saviing.bank.account.application.port.in.result.SavingsInfo;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "적금 정보 (적금 계좌인 경우에만 제공)")
public record SavingsDto(
    @Schema(description = "만기 시 출금계좌", example = "11012345678901234")
    String maturityWithdrawalAccount,

    @Schema(description = "목표금액 (원)", example = "1000000")
    Long targetAmount,

    @Schema(description = "적금 기간", example = "12")
    Integer termPeriod,

    @Schema(description = "적금 기간 단위", example = "WEEKS")
    String termPeriodUnit,

    @Schema(description = "만기일", example = "2024-12-31")
    LocalDate maturityDate
) {

    public static SavingsDto from(SavingsInfo savingsInfo) {
        if (savingsInfo == null) {
            return null;
        }

        return SavingsDto.builder()
            .maturityWithdrawalAccount(savingsInfo.maturityWithdrawalAccount())
            .targetAmount(savingsInfo.targetAmount())
            .termPeriod(savingsInfo.termPeriod())
            .termPeriodUnit(savingsInfo.termPeriodUnit())
            .maturityDate(savingsInfo.maturityDate())
            .build();
    }
}