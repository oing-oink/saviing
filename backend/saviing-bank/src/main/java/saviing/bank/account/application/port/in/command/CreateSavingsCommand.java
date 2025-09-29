package saviing.bank.account.application.port.in.command;

import saviing.bank.account.domain.vo.AccountNumber;
import saviing.bank.account.domain.vo.ProductId;
import saviing.bank.account.domain.vo.TermPeriod;
import saviing.bank.common.vo.MoneyWon;

/**
 * 적금 계좌 생성 명령
 *
 * 자유적금 계좌는 목표금액과 기간이 정해진 저축 계좌입니다.
 * 자유롭게 입금할 수 있으며, 만기 시 설정된 출금계좌로 자동 이체가 가능합니다.
 *
 * @param customerId 고객 ID
 * @param productId 상품 ID (적금 상품이어야 함)
 * @param targetAmount 목표금액 (달성하고자 하는 저축 목표)
 * @param termPeriod 적금 기간 (예: 15주, 12개월 등)
 * @param maturityWithdrawalAccount 만기 시 출금계좌 (nullable, 미설정 시 만기 후 수동 출금)
 * @param autoTransfer 초기 자동이체 설정 (nullable)
 */
public record CreateSavingsCommand(
    Long customerId,
    ProductId productId,
    MoneyWon targetAmount,
    TermPeriod termPeriod,
    AccountNumber maturityWithdrawalAccount,
    AutoTransferInitCommand autoTransfer // nullable
) implements CreateAccountCommand {
}
