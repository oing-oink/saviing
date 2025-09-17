package saviing.bank.transaction.application.port.out;

import java.util.Optional;

import saviing.bank.account.domain.model.Account;

/**
 * 계좌 데이터 로드 포트
 * 거래 도메인에서 계좌 정보를 조회하는 기능을 제공한다.
 */
public interface LoadAccountPort {

    /**
     * 계좌 ID로 계좌 정보를 조회한다
     *
     * @param accountId 조회할 계좌 ID
     * @return 계좌 엔티티 (Optional)
     */
    Optional<Account> loadAccount(Long accountId);
}