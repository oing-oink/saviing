package saviing.bank.account.exception;

import saviing.common.exception.BusinessException;
import saviing.common.exception.ErrorCode;

/**
 * 상품 도메인에서 발생하는 비즈니스 예외의 기본 클래스입니다.
 */
public abstract class ProductException extends BusinessException {

    protected ProductException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected ProductException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
