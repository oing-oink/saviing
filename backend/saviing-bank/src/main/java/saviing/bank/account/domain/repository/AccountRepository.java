package saviing.bank.account.domain.repository;

import java.util.List;
import java.util.Optional;

import saviing.bank.account.domain.model.Account;

public interface AccountRepository {
    
    /**
     * ID로 계좌를 조회한다.
     */
    Optional<Account> findById(Long id);
    
    /**
     * 계좌번호로 계좌를 조회한다.
     */
    Optional<Account> findByAccountNumber(String accountNumber);
    
    /**
     * 고객ID로 계좌 목록을 조회한다.
     */
    List<Account> findByCustomerId(Long customerId);
    
    /**
     * 계좌를 저장한다.
     */
    Account save(Account account);
    
    /**
     * 계좌를 삭제한다.
     */
    void delete(Account account);
    
    /**
     * ID로 계좌 존재 여부를 확인한다.
     */
    boolean existsById(Long id);
    
    /**
     * 계좌번호로 계좌 존재 여부를 확인한다.
     */
    boolean existsByAccountNumber(String accountNumber);
}