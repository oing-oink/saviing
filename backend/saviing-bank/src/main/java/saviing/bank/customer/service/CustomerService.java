package saviing.bank.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import saviing.bank.customer.repository.CustomerRepository;

import java.util.Optional;

/**
 * 고객 서비스
 * 고객 리포지토리를 통해 고객 이름 등 필요한 정보를 조회한다.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * 고객 ID로 고객 이름을 조회한다
     *
     * @param customerId 조회할 고객 ID
     * @return 고객 이름 (Optional)
     */
    public Optional<String> getCustomerName(Long customerId) {
        return customerRepository.findById(customerId)
                .map(customer -> customer.getName());
    }
}