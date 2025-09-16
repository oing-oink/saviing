package saviing.bank.account.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 계좌 출금 요청 DTO.
 */
public record WithdrawAccountRequest(
    @NotNull @Positive Long accountId,
    @NotNull @Positive Long amount
) {

    public WithdrawAccountRequest {
        if (accountId == null || accountId <= 0) {
            throw new IllegalArgumentException("accountId must be positive: " + accountId);
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
    }

    public static WithdrawAccountRequest of(Long accountId, Long amount) {
        return new WithdrawAccountRequest(accountId, amount);
    }
}