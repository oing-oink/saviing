package saviing.game.character.domain.exception;

import saviing.game.character.domain.model.vo.CustomerId;

/**
 * 활성 캐릭터가 이미 존재할 때 발생하는 예외
 */
public class DuplicateActiveCharacterException extends CharacterException {
    
    public DuplicateActiveCharacterException() {
        super(CharacterErrorCode.DUPLICATE_ACTIVE_CHARACTER);
    }
    
    public DuplicateActiveCharacterException(String message) {
        super(CharacterErrorCode.DUPLICATE_ACTIVE_CHARACTER, message);
    }
    
    public DuplicateActiveCharacterException(CustomerId customerId) {
        super(CharacterErrorCode.DUPLICATE_ACTIVE_CHARACTER, 
              "고객당 하나의 활성 캐릭터만 존재할 수 있습니다. 고객 ID: " + customerId.value());
    }
}