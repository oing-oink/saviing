package saviing.bank.account.application.port.in;

import saviing.bank.account.application.port.in.result.GetAccountResult;

public interface GetAccountUseCase {

    /**
     * 계좌 ID로 계좌 상세 정보를 조회합니다.
     *
     * 계좌 정보와 해당 계좌의 상품 정보를 함께 조회하여
     * 클라이언트에서 필요한 모든 정보를 한번에 제공합니다.
     *
     * @param accountId 조회할 계좌 ID
     * @return 계좌 상세 정보 (계좌 정보 + 상품 정보)
     */
    GetAccountResult getAccount(Long accountId);

    /**
     * 계좌번호로 계좌 상세 정보를 조회합니다.
     *
     * 계좌 정보와 해당 계좌의 상품 정보를 함께 조회하여
     * 클라이언트에서 필요한 모든 정보를 한번에 제공합니다.
     *
     * @param accountNumber 조회할 계좌번호
     * @return 계좌 상세 정보 (계좌 정보 + 상품 정보)
     */
    GetAccountResult getAccountByNumber(String accountNumber);
}