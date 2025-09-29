package saviing.bank.customer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import saviing.bank.customer.entity.Customer;
import saviing.bank.customer.repository.CustomerRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void 존재하는_고객의_이름을_조회할_수_있다() {
        // Given
        Long customerId = 1L;
        String expectedName = "홍길동";
        Customer customer = Customer.builder()
            .name(expectedName)
            .build();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        Optional<String> result = customerService.getCustomerName(customerId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedName);
    }

    @Test
    void 존재하지_않는_고객의_경우_빈_값을_반환한다() {
        // Given
        Long customerId = 999L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When
        Optional<String> result = customerService.getCustomerName(customerId);

        // Then
        assertThat(result).isEmpty();
    }
}