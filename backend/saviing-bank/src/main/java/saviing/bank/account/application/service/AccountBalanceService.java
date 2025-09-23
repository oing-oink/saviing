package saviing.bank.account.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.bank.account.application.event.SavingsDepositEventPublisher;
import saviing.bank.account.application.port.in.command.DepositAccountCommand;
import saviing.bank.account.application.port.in.command.WithdrawAccountCommand;
import saviing.bank.account.application.port.in.result.BalanceUpdateResult;
import saviing.bank.account.application.port.out.LoadAccountPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.AccountStatus;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.exception.InvalidAccountStateException;
import saviing.bank.common.vo.MoneyWon;
import saviing.common.annotation.ExecutionTime;

/**
 * 계좌 거래 응용 서비스.
 *
 * 기술적 관심사를 처리합니다:
 * - 트랜잭션 관리
 * - 비즈니스 상태 검증 (ACTIVE 계좌만 거래 가능)
 * - 결과 변환 (도메인 객체 → 기본 타입 DTO)
 */
@Slf4j
@ExecutionTime
@Service
@RequiredArgsConstructor
public class AccountBalanceService {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final SavingsDepositEventPublisher savingsDepositEventPublisher;

    /**
     * 계좌 출금을 처리합니다.
     *
     * @param accountId 계좌 ID
     * @param amount 출금 금액 (원 단위)
     * @return 잔액 업데이트 결과
     */
    @Transactional
    public BalanceUpdateResult withdraw(Long accountId, Long amount) {
        WithdrawAccountCommand command = WithdrawAccountCommand.of(accountId, amount);

        // 계좌 로드 및 상태 검증
        Account account = getActiveAccount(command.accountId());
        MoneyWon previousBalance = account.getBalance();

        // 출금 수행 (도메인 검증 포함)
        account.withdraw(command.amount());

        // 저장
        saveAccountPort.save(account);

        // 결과 변환
        return BalanceUpdateResult.from(account, previousBalance, command.amount());
    }

    /**
     * 계좌 입금을 처리합니다.
     *
     * @param accountId 계좌 ID
     * @param amount 입금 금액 (원 단위)
     * @return 잔액 업데이트 결과
     */
    @Transactional
    public BalanceUpdateResult deposit(Long accountId, Long amount) {
        DepositAccountCommand command = DepositAccountCommand.of(accountId, amount);

        // 계좌 로드 및 상태 검증
        Account account = getActiveAccount(command.accountId());
        MoneyWon previousBalance = account.getBalance();

        // 입금 수행
        account.deposit(command.amount());

        // 저장
        Account savedAccount = saveAccountPort.save(account);

        // 게임 적금 적립 이벤트 발행 (실패해도 본 거래에는 영향 없음)
        savingsDepositEventPublisher.publish(savedAccount, command.amount());

        // 결과 변환
        return BalanceUpdateResult.from(savedAccount, previousBalance, command.amount());
    }


    /**
     * ACTIVE 상태의 계좌만 반환합니다.
     */
    private Account getActiveAccount(AccountId accountId) {
        Account account = loadAccountPort.findById(accountId)
            .orElseThrow(() -> new InvalidAccountStateException(Map.of(
                "accountId", accountId.value(),
                "reason", "NOT_FOUND"
            )));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException(Map.of(
                "accountId", accountId.value(),
                "status", account.getStatus()
            ));
        }

        return account;
    }
}