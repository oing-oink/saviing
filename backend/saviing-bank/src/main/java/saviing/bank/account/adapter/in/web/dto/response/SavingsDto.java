package saviing.bank.account.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
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
    LocalDate maturityDate,

    @Schema(description = "자동이체 정보")
    AutoTransferDto autoTransfer
) {

    public static SavingsDto from(SavingsInfo savingsInfo) {
        if (savingsInfo == null) {
            return null;
        }

        AutoTransferDto autoTransferDto = null;
        if (savingsInfo.autoTransfer() != null) {
            autoTransferDto = AutoTransferDto.builder()
                .enabled(savingsInfo.autoTransfer().enabled())
                .cycle(savingsInfo.autoTransfer().cycle())
                .transferDay(savingsInfo.autoTransfer().transferDay())
                .amount(savingsInfo.autoTransfer().amount())
                .nextRunDate(savingsInfo.autoTransfer().nextRunDate())
                .lastExecutedAt(savingsInfo.autoTransfer().lastExecutedAt())
                .build();
        }

        return SavingsDto.builder()
            .maturityWithdrawalAccount(savingsInfo.maturityWithdrawalAccount())
            .targetAmount(savingsInfo.targetAmount())
            .termPeriod(savingsInfo.termPeriod())
            .termPeriodUnit(savingsInfo.termPeriodUnit())
            .maturityDate(savingsInfo.maturityDate())
            .autoTransfer(autoTransferDto)
            .build();
    }

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "자동이체 요약 정보")
    public record AutoTransferDto(
        @Schema(description = "활성화 여부", example = "true")
        Boolean enabled,

        @Schema(description = "주기", example = "WEEKLY")
        String cycle,

        @Schema(description = "납부 일자", example = "3")
        Integer transferDay,

        @Schema(description = "납부 금액", example = "100000")
        Long amount,

        @Schema(description = "다음 실행 예정일", example = "2024-02-14")
        LocalDate nextRunDate,

        @Schema(description = "마지막 실행 시각", example = "2024-01-07T09:00:00Z")
        Instant lastExecutedAt
    ) {
    }
}
