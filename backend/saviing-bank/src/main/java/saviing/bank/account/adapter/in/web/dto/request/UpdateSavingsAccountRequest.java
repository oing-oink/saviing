package saviing.bank.account.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

import saviing.bank.account.application.port.in.command.UpdateSavingsAccountCommand;

/**
 * 적금 계좌의 목표 금액과 만기 출금 계좌를 수정하기 위한 요청입니다.
 */
public record UpdateSavingsAccountRequest(
    @Schema(description = "변경할 목표 금액", example = "1500000")
    @Positive(message = "목표 금액은 0보다 커야 합니다")
    Long targetAmount,

    @Schema(description = "변경할 만기 출금 계좌번호", example = "11012345678901234")
    String maturityWithdrawalAccount
) {

    /**
     * 요청 값을 수정 명령으로 변환합니다.
     *
     * @param accountId 수정할 계좌 ID
     * @return 수정 명령
     */
    public UpdateSavingsAccountCommand toCommand(Long accountId) {
        return UpdateSavingsAccountCommand.of(accountId, targetAmount, maturityWithdrawalAccount);
    }
}
