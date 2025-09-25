package saviing.game.pet.domain.model.vo;

import saviing.game.pet.domain.exception.PetInvalidValueException;

/**
 * 펫 식별자 Value Object
 * pet 테이블의 inventory_item_id와 매핑됩니다.
 */
public record PetId(Long value) {
    public PetId {
        if (value == null || value <= 0) {
            throw PetInvalidValueException.invalidPetId(value);
        }
    }

    public static PetId of(Long value) {
        return new PetId(value);
    }
}