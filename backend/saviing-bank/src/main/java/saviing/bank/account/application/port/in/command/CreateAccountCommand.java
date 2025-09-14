package saviing.bank.account.application.port.in.command;

import saviing.bank.account.domain.model.ProductType;

public record CreateAccountCommand(
    Long customerId,
    ProductType productType
) {
    public static CreateAccountCommand of(Long customerId, String productTypeStr) {
        return new CreateAccountCommand(
            customerId,
            ProductType.valueOf(productTypeStr)
        );
    }
}