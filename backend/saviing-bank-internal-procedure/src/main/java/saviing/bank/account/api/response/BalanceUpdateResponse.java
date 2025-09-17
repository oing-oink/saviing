package saviing.bank.account.api.response;

/**
 * 잔액 업데이트 응답 DTO.
 */
public record BalanceUpdateResponse(
    Long accountId,
    Long previousBalance,
    Long currentBalance,
    Long transactionAmount
) {

    public static BalanceUpdateResponse of(
        Long accountId,
        Long previousBalance,
        Long currentBalance,
        Long transactionAmount
    ) {
        return new BalanceUpdateResponse(
            accountId,
            previousBalance,
            currentBalance,
            transactionAmount
        );
    }
}