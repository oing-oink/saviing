package saviing.bank.account.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import saviing.bank.account.adapter.out.persistence.entity.AccountJpaEntity;
import saviing.bank.account.adapter.out.persistence.repository.JpaAccountRepository;
import saviing.bank.account.application.port.out.LoadAccountPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AccountNumber;

@Repository
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements LoadAccountPort, SaveAccountPort {

    private final JpaAccountRepository jpaAccountRepository;
    
    @Override
    public Optional<Account> findById(AccountId id) {
        return jpaAccountRepository.findById(id.value())
            .map(AccountJpaEntity::toDomain);
    }
    
    @Override
    public Optional<Account> findByAccountNumber(AccountNumber accountNumber) {
        return jpaAccountRepository.findByAccountNumber(accountNumber.value())
            .map(AccountJpaEntity::toDomain);
    }
    
    @Override
    public List<Account> findByCustomerId(Long customerId) {
        return jpaAccountRepository.findByCustomerId(customerId)
            .stream()
            .map(AccountJpaEntity::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByAccountNumber(AccountNumber accountNumber) {
        return jpaAccountRepository.existsByAccountNumber(accountNumber.value());
    }
    
    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = AccountJpaEntity.fromDomain(account);
        AccountJpaEntity saved = jpaAccountRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public void delete(Account account) {
        AccountJpaEntity entity = AccountJpaEntity.fromDomain(account);
        jpaAccountRepository.delete(entity);
    }
}