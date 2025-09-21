package saviing.bank.transaction.application.port.out;

import java.util.Optional;

/**
 * 고객 이름 조회 포트
 * 거래 도메인에서 고객의 이름 정보를 조회하는 기능을 제공한다.
 */
public interface LoadCustomerNamePort {

    /**
     * 고객 ID로 고객 이름을 조회한다
     *
     * @param customerId 조회할 고객 ID
     * @return 고객 이름 (Optional)
     */
    Optional<String> loadCustomerName(Long customerId);
}