package saviing.bank.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saviing.bank.customer.entity.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByOauth2ProviderAndOauth2Id(Customer.OAuth2Provider oauth2Provider, String oauth2Id);

    boolean existsByOauth2ProviderAndOauth2Id(Customer.OAuth2Provider oauth2Provider, String oauth2Id);
}