package saviing.game.pet.domain.model.vo;

import saviing.game.pet.domain.exception.PetInvalidNameException;

/**
 * 펫 이름 Value Object
 */
public record PetName(String value) {
    private static final int MAX_LENGTH = 20;

    public PetName {
        if (value == null) {
            throw PetInvalidNameException.nullName();
        }
        if (value.trim().isEmpty()) {
            throw PetInvalidNameException.emptyName();
        }
        if (value.trim().length() > MAX_LENGTH) {
            throw PetInvalidNameException.tooLong(value);
        }
    }

    // 펫 이름 수정 시 사용
    public static PetName of(String name) {
        return new PetName(name);
    }

    /**
     * 아이템 이름으로 펫 이름을 생성합니다.
     * 펫 생성 시 기본 이름으로 사용됩니다.
     */
    public static PetName fromItemName(String itemName) {
        return new PetName(itemName.trim());
    }
}