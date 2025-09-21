package saviing.bank.account.application.port.in.command;

import saviing.bank.account.domain.model.AutoTransferCycle;
import saviing.bank.common.vo.MoneyWon;

/**
 * 자동이체 스케줄을 수정하기 위한 명령 객체.
 *
 * @param accountId 계좌 ID
 * @param enabled 자동이체 활성화 여부
 * @param cycle 자동이체 주기 (활성화 시 필수)
 * @param transferDay 납부 일자 (활성화 시 필수, 주간은 1~7, 월간은 1~31)
 * @param amount 자동이체 금액 (활성화 시 필수)
 * @param withdrawAccountId 출금 계좌 ID (활성화 시 필수)
 */
public record UpdateAutoTransferScheduleCommand(
    Long accountId,
    boolean enabled,
    AutoTransferCycle cycle,
    Integer transferDay,
    MoneyWon amount,
    Long withdrawAccountId
) {
}
