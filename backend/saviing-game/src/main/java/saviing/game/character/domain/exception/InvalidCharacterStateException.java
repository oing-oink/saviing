package saviing.game.character.domain.exception;

/**
 * 캐릭터 상태가 올바르지 않을 때 발생하는 예외
 */
public class InvalidCharacterStateException extends CharacterException {
    
    public InvalidCharacterStateException() {
        super(CharacterErrorCode.CHARACTER_INVALID_STATE);
    }
    
    public InvalidCharacterStateException(String message) {
        super(CharacterErrorCode.CHARACTER_INVALID_STATE, message);
    }
    
    public InvalidCharacterStateException(CharacterErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    /**
     * 캐릭터가 활성 상태가 아닐 때
     */
    public static InvalidCharacterStateException characterNotActive() {
        return new InvalidCharacterStateException(CharacterErrorCode.CHARACTER_NOT_ACTIVE, 
                                                 "캐릭터가 활성 상태가 아닙니다");
    }
    
    /**
     * 올바르지 않은 코인 수량일 때
     */
    public static InvalidCharacterStateException invalidCoinAmount(String message) {
        return new InvalidCharacterStateException(CharacterErrorCode.CHARACTER_INVALID_COIN_AMOUNT, message);
    }
    
    /**
     * 올바르지 않은 피쉬 코인 수량일 때
     */
    public static InvalidCharacterStateException invalidFishCoinAmount(String message) {
        return new InvalidCharacterStateException(CharacterErrorCode.CHARACTER_INVALID_FISH_COIN_AMOUNT, message);
    }
}