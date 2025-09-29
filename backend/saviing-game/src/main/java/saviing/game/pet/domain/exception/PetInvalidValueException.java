package saviing.game.pet.domain.exception;

/**
 * 펫 값 객체 검증 실패 시 발생하는 예외
 */
public class PetInvalidValueException extends PetException {

    public PetInvalidValueException(PetErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static PetInvalidValueException invalidAffection(int value) {
        return new PetInvalidValueException(
            PetErrorCode.PET_INVALID_AFFECTION_VALUE,
            String.format("애정도는 0 이상 %d 이하여야 합니다. 입력된 값: %d", 100, value)
        );
    }

    public static PetInvalidValueException invalidEnergy(int value) {
        return new PetInvalidValueException(
            PetErrorCode.PET_INVALID_ENERGY_VALUE,
            String.format("포만감은 0 이상 %d 이하여야 합니다. 입력된 값: %d", 100, value)
        );
    }

    public static PetInvalidValueException invalidExperience(int value) {
        return new PetInvalidValueException(
            PetErrorCode.PET_INVALID_EXPERIENCE_VALUE,
            String.format("경험치는 0 이상이어야 합니다. 입력된 값: %d", value)
        );
    }

    public static PetInvalidValueException invalidLevel(int value) {
        return new PetInvalidValueException(
            PetErrorCode.PET_INVALID_LEVEL_VALUE,
            String.format("레벨은 1 이상 %d 이하여야 합니다. 입력된 값: %d", 20, value)
        );
    }

    public static PetInvalidValueException invalidPetId(Long value) {
        return new PetInvalidValueException(
            PetErrorCode.PET_INVALID_PET_ID_VALUE,
            String.format("펫 ID는 양수여야 합니다. 입력된 값: %s", value)
        );
    }
}