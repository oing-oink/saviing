package saviing.bank.transaction.domain.model.account;

import saviing.bank.common.vo.MoneyWon;

/**
 * AccountInternalApi에서 조회한 최소 정보를 담는 스냅샷.
 */
public record AccountSnapshot(
    Long accountId,
    MoneyWon balance,
    AccountStatusSnapshot status
) {
}
