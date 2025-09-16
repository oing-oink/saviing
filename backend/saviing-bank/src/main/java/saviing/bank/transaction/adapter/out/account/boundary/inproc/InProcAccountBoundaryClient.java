package saviing.bank.transaction.adapter.out.account.boundary.inproc;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import saviing.bank.account.application.port.in.GetAccountUseCase;
import saviing.bank.account.application.port.in.result.GetAccountResult;
import saviing.bank.account.application.port.out.LoadAccountPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.common.vo.MoneyWon;
import saviing.bank.transaction.adapter.out.account.boundary.AccountBoundaryClient;
import saviing.bank.transaction.domain.model.TransactionDirection;
import saviing.bank.transaction.exception.TransactionNotFoundException;

import java.util.Map;

/**
 * In-proc 구현: 실제 HTTP 없이 내부 UseCase/Port를 호출하여 경계 호출을 시뮬레이션한다.
 */
@Component
@RequiredArgsConstructor
public class InProcAccountBoundaryClient implements AccountBoundaryClient {

    private final GetAccountUseCase getAccountUseCase;
    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;

    @Override
    public GetAccountResult getByNumber(String accountNumber) {
        return getAccountUseCase.getAccountByNumber(accountNumber);
    }

    @Override
    public Account getById(Long accountId) {
        return loadAccountPort.findById(AccountId.of(accountId))
            .orElseThrow(() -> new TransactionNotFoundException(Map.of("accountId", accountId)));
    }

    @Override
    public void applyBalanceEffect(Long accountId, MoneyWon amount, TransactionDirection direction) {
        Account account = getById(accountId);
        if (direction == TransactionDirection.CREDIT) {
            account.deposit(amount);
        } else {
            account.withdraw(amount);
        }
        saveAccountPort.save(account);
    }
}
