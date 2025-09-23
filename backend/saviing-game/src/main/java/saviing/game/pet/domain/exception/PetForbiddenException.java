package saviing.game.pet.domain.exception;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.pet.domain.model.vo.PetId;

/**
 * 펫 접근 권한이 없을 때 발생하는 예외
 */
public class PetForbiddenException extends PetException {

    public PetForbiddenException() {
        super(PetErrorCode.PET_ACCESS_FORBIDDEN);
    }

    public PetForbiddenException(String message) {
        super(PetErrorCode.PET_ACCESS_FORBIDDEN, message);
    }

    public PetForbiddenException(PetId petId, CharacterId characterId) {
        super(PetErrorCode.PET_ACCESS_FORBIDDEN,
            String.format("펫 접근 권한이 없습니다. PetId: %d, CharacterId: %d",
                petId.value(), characterId.value()));
    }
}