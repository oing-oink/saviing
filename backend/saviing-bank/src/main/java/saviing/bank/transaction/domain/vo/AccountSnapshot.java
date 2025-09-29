package saviing.bank.transaction.domain.vo;

import saviing.bank.common.vo.MoneyWon;

/**
 * AccountInternalApi에서 조회한 최소 정보를 담는 스냅샷.
 */
public record AccountSnapshot(
    Long accountId,
    Long customerId,
    MoneyWon balance,
    AccountStatusSnapshot status
) {
}
