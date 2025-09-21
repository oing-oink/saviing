package saviing.bank.transaction.adapter.out.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import saviing.bank.customer.service.CustomerService;
import saviing.bank.transaction.application.port.out.LoadCustomerNamePort;

import java.util.Optional;

/**
 * 고객 정보 조회 어댑터
 * 거래 도메인에서 고객 모듈의 CustomerService를 통해 고객 이름을 조회한다.
 */
@Component
@RequiredArgsConstructor
public class TransactionCustomerAdapter implements LoadCustomerNamePort {

    private final CustomerService customerService;

    @Override
    public Optional<String> loadCustomerName(Long customerId) {
        return customerService.getCustomerName(customerId);
    }
}