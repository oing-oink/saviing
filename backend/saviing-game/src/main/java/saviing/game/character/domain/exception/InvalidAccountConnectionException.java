package saviing.game.character.domain.exception;

/**
 * 계좌 연결 상태가 올바르지 않을 때 발생하는 예외
 */
public class InvalidAccountConnectionException extends CharacterException {
    
    public InvalidAccountConnectionException(CharacterErrorCode errorCode) {
        super(errorCode);
    }
    
    public InvalidAccountConnectionException(CharacterErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    /**
     * 계좌가 연결되어 있지 않을 때
     */
    public static InvalidAccountConnectionException accountNotConnected() {
        return new InvalidAccountConnectionException(CharacterErrorCode.ACCOUNT_NOT_CONNECTED);
    }
    
    /**
     * 계좌가 이미 연결되어 있을 때
     */
    public static InvalidAccountConnectionException accountAlreadyConnected() {
        return new InvalidAccountConnectionException(CharacterErrorCode.ACCOUNT_ALREADY_CONNECTED);
    }
    
    /**
     * 계좌 연결이 이미 진행 중일 때
     */
    public static InvalidAccountConnectionException connectionInProgress() {
        return new InvalidAccountConnectionException(CharacterErrorCode.ACCOUNT_CONNECTION_IN_PROGRESS);
    }
    
    /**
     * 계좌 연결 상태가 유효하지 않을 때
     */
    public static InvalidAccountConnectionException invalidConnectionState(String message) {
        return new InvalidAccountConnectionException(CharacterErrorCode.INVALID_ACCOUNT_CONNECTION_STATE, message);
    }
}