package saviing.bank.account.application.port.in.command;

import saviing.bank.account.domain.model.AutoTransferCycle;
import saviing.bank.common.vo.MoneyWon;

/**
 * 적금 계좌 개설 시 초기 자동이체 설정을 전달하는 명령 객체.
 *
 * @param enabled 자동이체 활성화 여부
 * @param cycle 자동이체 주기 (활성화된 경우 필수)
 * @param transferDay 납부 일자 (활성화된 경우 필수)
 * @param amount 자동이체 금액 (활성화된 경우 필수)
 */
public record AutoTransferInitCommand(
    boolean enabled,
    AutoTransferCycle cycle,
    Integer transferDay,
    MoneyWon amount
) {
}
