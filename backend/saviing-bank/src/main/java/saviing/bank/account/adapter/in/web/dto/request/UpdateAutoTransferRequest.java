package saviing.bank.account.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import saviing.bank.account.application.port.in.command.UpdateAutoTransferScheduleCommand;
import saviing.bank.account.domain.model.AutoTransferCycle;
import saviing.bank.common.vo.MoneyWon;

/**
 * 자동이체 설정 수정 요청 DTO.
 *
 * @param enabled 자동이체 활성화 여부
 * @param cycle 자동이체 주기 (활성화 시 필수)
 * @param transferDay 납부 일자 (활성화 시 필수)
 * @param amount 자동이체 금액 (활성화 시 필수)
 */
@Schema(description = "자동이체 설정 수정 요청")
public record UpdateAutoTransferRequest(
    @Schema(description = "자동이체 활성화 여부", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "자동이체 활성화 여부는 필수입니다")
    Boolean enabled,

    @Schema(description = "자동이체 주기", example = "WEEKLY", allowableValues = {"WEEKLY", "MONTHLY"})
    String cycle,

    @Schema(description = "자동이체 납부 일자", example = "3")
    Integer transferDay,

    @Schema(description = "자동이체 금액", example = "100000")
    Long amount
) {

    /**
     * 요청 DTO를 명령 객체로 변환한다.
     *
     * @param accountId 계좌 ID
     * @return 자동이체 수정 명령
     */
    public UpdateAutoTransferScheduleCommand toCommand(Long accountId) {
        boolean isEnabled = Boolean.TRUE.equals(enabled);
        if (isEnabled) {
            if (cycle == null || transferDay == null || amount == null) {
                throw new IllegalArgumentException("자동이체를 활성화하려면 주기, 납부일, 금액이 모두 필요합니다");
            }
            AutoTransferCycle cycleEnum = AutoTransferCycle.valueOf(cycle.toUpperCase());
            return new UpdateAutoTransferScheduleCommand(
                accountId,
                true,
                cycleEnum,
                transferDay,
                MoneyWon.of(amount)
            );
        }

        return new UpdateAutoTransferScheduleCommand(
            accountId,
            false,
            null,
            null,
            null
        );
    }
}
