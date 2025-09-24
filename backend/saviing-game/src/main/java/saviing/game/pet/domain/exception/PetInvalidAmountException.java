package saviing.game.pet.domain.exception;

/**
 * 펫 능력치 변경량이 올바르지 않을 때 발생하는 예외
 */
public class PetInvalidAmountException extends PetException {

    public PetInvalidAmountException(PetErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static PetInvalidAmountException invalidAffectionAmount(int amount, String operation) {
        return new PetInvalidAmountException(
            PetErrorCode.PET_INVALID_AFFECTION_AMOUNT,
            String.format("애정도 %s량은 음수일 수 없습니다. 입력된 값: %d", operation, amount)
        );
    }

    public static PetInvalidAmountException invalidEnergyAmount(int amount, String operation) {
        return new PetInvalidAmountException(
            PetErrorCode.PET_INVALID_ENERGY_AMOUNT,
            String.format("포만감 %s량은 음수일 수 없습니다. 입력된 값: %d", operation, amount)
        );
    }

    public static PetInvalidAmountException invalidExperienceAmount(int amount) {
        return new PetInvalidAmountException(
            PetErrorCode.PET_INVALID_EXPERIENCE_AMOUNT,
            String.format("경험치 증가량은 음수일 수 없습니다. 입력된 값: %d", amount)
        );
    }
}