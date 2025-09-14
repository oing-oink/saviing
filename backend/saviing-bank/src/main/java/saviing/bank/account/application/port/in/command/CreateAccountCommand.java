package saviing.bank.account.application.port.in.command;

import saviing.bank.account.domain.vo.ProductId;

public record CreateAccountCommand(
    Long customerId,
    ProductId productId
) {
    public static CreateAccountCommand of(Long customerId, Long productId) {
        return new CreateAccountCommand(customerId, ProductId.of(productId));
    }
}