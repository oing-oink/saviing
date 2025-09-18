package saviing.bank.account.api;

import saviing.bank.account.api.request.DepositAccountRequest;
import saviing.bank.account.api.request.WithdrawAccountRequest;
import saviing.bank.account.api.request.GetAccountRequest;
import saviing.bank.account.api.response.AccountApiResponse;
import saviing.bank.account.api.response.AccountInfoResponse;
import saviing.bank.account.api.response.BalanceUpdateResponse;

/**
 * Account 도메인의 내부 API 인터페이스.
 */
public interface AccountInternalApi {

    /**
     * 계좌 출금 처리.
     *
     * @param request 출금 요청
     * @return 처리 결과
     */
    AccountApiResponse<BalanceUpdateResponse> withdraw(WithdrawAccountRequest request);

    /**
     * 계좌 입금 처리.
     *
     * @param request 입금 요청
     * @return 처리 결과
     */
    AccountApiResponse<BalanceUpdateResponse> deposit(DepositAccountRequest request);

    /**
     * 계좌 정보 조회.
     *
     * @param request 조회 요청
     * @return 조회 결과
     */
    AccountApiResponse<AccountInfoResponse> getAccount(GetAccountRequest request);
}