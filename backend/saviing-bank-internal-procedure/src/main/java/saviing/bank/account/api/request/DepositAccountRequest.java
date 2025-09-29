package saviing.bank.account.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 계좌 입금 요청 DTO.
 */
public record DepositAccountRequest(
    @NotNull @Positive Long accountId,
    @NotNull @Positive Long amount
) {

    public DepositAccountRequest {
        if (accountId == null || accountId <= 0) {
            throw new IllegalArgumentException("accountId must be positive: " + accountId);
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
    }

    public static DepositAccountRequest of(Long accountId, Long amount) {
        return new DepositAccountRequest(accountId, amount);
    }
}