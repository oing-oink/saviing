package saviing.bank.account.api.response;

/**
 * 계좌 조회 응답 DTO.
 */
public record AccountInfoResponse(
    Long accountId,
    String accountNumber,
    Long customerId,
    Long balance,
    String status,
    Long productId
) {
}