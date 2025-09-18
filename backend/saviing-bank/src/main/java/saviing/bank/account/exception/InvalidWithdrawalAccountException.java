package saviing.bank.account.exception;

import java.util.Map;

/**
 * 만기 출금 계좌가 비즈니스 규칙을 만족하지 않을 때 발생하는 예외입니다.
 */
public class InvalidWithdrawalAccountException extends AccountException {

    public InvalidWithdrawalAccountException(Map<String, Object> context) {
        super(AccountErrorType.INVALID_WITHDRAWAL_ACCOUNT, context);
    }
}
