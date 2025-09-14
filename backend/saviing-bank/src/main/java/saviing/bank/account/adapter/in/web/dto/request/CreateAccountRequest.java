package saviing.bank.account.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;

import saviing.bank.account.application.port.in.command.CreateAccountCommand;

public record CreateAccountRequest(
    @NotNull(message = "고객ID는 필수입니다")
    Long customerId,
    
    @NotNull(message = "상품유형은 필수입니다")
    Long productId
) {
    public CreateAccountCommand toCommand() {
        return CreateAccountCommand.of(customerId, productId);
    }
}