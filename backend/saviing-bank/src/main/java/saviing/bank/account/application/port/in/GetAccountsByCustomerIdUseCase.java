package saviing.bank.account.application.port.in;

import java.util.List;

import saviing.bank.account.application.port.in.result.GetAccountResult;

public interface GetAccountsByCustomerIdUseCase {
    
    /**
     * 고객 ID로 해당 고객의 계좌 목록을 조회합니다.
     *
     * 계좌 정보와 해당 계좌의 상품 정보를 함께 조회하여
     * 클라이언트에서 필요한 모든 정보를 한번에 제공합니다.
     *
     * @param customerId 조회할 고객의 ID
     * @return 고객이 보유한 계좌 목록 (계좌 정보 + 상품 정보)
     */
    List<GetAccountResult> getAccountsByCustomerId(Long customerId);
}