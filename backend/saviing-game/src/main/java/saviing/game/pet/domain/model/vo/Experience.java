package saviing.game.pet.domain.model.vo;

import saviing.game.pet.domain.exception.PetInvalidValueException;
import saviing.game.pet.domain.exception.PetInvalidAmountException;

/**
 * 펫 경험치 Value Object
 */
public record Experience(int value) {
    private static final int MIN_EXPERIENCE = 0;

    public Experience {
        if (value < MIN_EXPERIENCE) {
            throw PetInvalidValueException.invalidExperience(value);
        }
    }

    public static Experience of(int experience) {
        return new Experience(experience);
    }

    public static Experience initial() {
        return new Experience(MIN_EXPERIENCE);
    }

    public Experience add(Experience amount) {
        if (amount.value < 0) {
            throw PetInvalidAmountException.invalidExperienceAmount(amount.value);
        }
        return new Experience(value + amount.value);
    }

    public boolean isGreaterThanOrEqual(Experience targetExp) {
        return value >= targetExp.value;
    }
}