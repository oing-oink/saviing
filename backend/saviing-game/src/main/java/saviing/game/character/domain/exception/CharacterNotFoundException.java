package saviing.game.character.domain.exception;

/**
 * 캐릭터를 찾을 수 없을 때 발생하는 예외
 */
public class CharacterNotFoundException extends CharacterException {
    
    public CharacterNotFoundException() {
        super(CharacterErrorCode.CHARACTER_NOT_FOUND);
    }
    
    public CharacterNotFoundException(String message) {
        super(CharacterErrorCode.CHARACTER_NOT_FOUND, message);
    }
    
    public CharacterNotFoundException(Long characterId) {
        super(CharacterErrorCode.CHARACTER_NOT_FOUND, "캐릭터를 찾을 수 없습니다. ID: " + characterId);
    }
}