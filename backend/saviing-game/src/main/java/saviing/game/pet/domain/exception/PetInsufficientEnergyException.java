package saviing.game.pet.domain.exception;

/**
 * 펫의 포만감이 부족할 때 발생하는 예외
 */
public class PetInsufficientEnergyException extends PetException {

    public PetInsufficientEnergyException() {
        super(PetErrorCode.PET_INSUFFICIENT_ENERGY);
    }

    public PetInsufficientEnergyException(String message) {
        super(PetErrorCode.PET_INSUFFICIENT_ENERGY, message);
    }

    public PetInsufficientEnergyException(int currentEnergy, int requiredEnergy) {
        super(PetErrorCode.PET_INSUFFICIENT_ENERGY,
            String.format("펫의 포만감이 부족합니다. 현재: %d, 필요: %d", currentEnergy, requiredEnergy));
    }
}