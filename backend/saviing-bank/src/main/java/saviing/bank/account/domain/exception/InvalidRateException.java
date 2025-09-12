package saviing.bank.account.domain.exception;

public class InvalidRateException extends DomainException {
    
    public InvalidRateException(String message) {
        super(DomainErrorCode.INVALID_RATE, message);
    }
    
    public InvalidRateException(short basisPoints) {
        super(DomainErrorCode.INVALID_RATE, 
            String.format("유효하지 않은 금리입니다: %d bp", basisPoints));
    }
    
    public static InvalidRateException negativeRate(short basisPoints) {
        return new InvalidRateException(String.format("금리는 0 이상이어야 합니다: %d bp", basisPoints));
    }
    
    public static InvalidRateException excessiveRate(short basisPoints, short maxRate) {
        return new InvalidRateException(String.format("금리가 허용 범위를 초과합니다: %d bp (최대: %d bp)", basisPoints, maxRate));
    }
}