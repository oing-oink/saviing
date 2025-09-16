package saviing.bank.account.application.port.in.command;

import saviing.bank.account.domain.vo.ProductId;

/**
 * 자유입출금 계좌 생성 명령
 *
 * 자유입출금 계좌는 목표금액, 기간 등의 제약이 없는 일반적인 예금 계좌입니다.
 * 입출금이 자유롭고, 이자율은 상품에 따라 결정됩니다.
 *
 * @param customerId 고객 ID
 * @param productId 상품 ID (자유입출금 상품이어야 함)
 */
public record CreateDemandDepositCommand(
    Long customerId,
    ProductId productId
) implements CreateAccountCommand {
}