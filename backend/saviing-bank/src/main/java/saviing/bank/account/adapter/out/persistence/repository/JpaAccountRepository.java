package saviing.bank.account.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import saviing.bank.account.adapter.out.persistence.entity.AccountJpaEntity;

public interface JpaAccountRepository extends JpaRepository<AccountJpaEntity, Long> {
    
    Optional<AccountJpaEntity> findByAccountNumber(String accountNumber);
    
    List<AccountJpaEntity> findByCustomerId(Long customerId);
    
    boolean existsByAccountNumber(String accountNumber);
}