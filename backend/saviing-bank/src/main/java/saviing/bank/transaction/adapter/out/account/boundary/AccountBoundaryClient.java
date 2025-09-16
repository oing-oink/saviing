package saviing.bank.transaction.adapter.out.account.boundary;

import saviing.bank.account.application.port.in.result.GetAccountResult;
import saviing.bank.account.domain.model.Account;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.domain.model.TransactionDirection;

/**
 * 모놀리식 환경에서 실제 HTTP 호출 없이, 계좌 API 경계를 시뮬레이션하기 위한 클라이언트 계약.
 * 구현체는 in-proc(내부 호출) 또는 추후 REST 클라이언트로 교체 가능하다.
 */
public interface AccountBoundaryClient {

    GetAccountResult getByNumber(String accountNumber);

    Account getById(Long accountId);

    void applyBalanceEffect(Long accountId, MoneyWon amount, TransactionDirection direction);
}
