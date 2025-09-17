package saviing.bank.account.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 계좌 조회 요청 DTO.
 */
public record GetAccountRequest(
    @NotNull @Positive Long accountId
) {

    public GetAccountRequest {
        if (accountId == null || accountId <= 0) {
            throw new IllegalArgumentException("accountId must be positive: " + accountId);
        }
    }

    public static GetAccountRequest of(Long accountId) {
        return new GetAccountRequest(accountId);
    }
}