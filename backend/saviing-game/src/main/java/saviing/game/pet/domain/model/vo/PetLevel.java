package saviing.game.pet.domain.model.vo;

import saviing.game.pet.domain.exception.PetInvalidValueException;
import saviing.game.pet.domain.exception.PetMaxLevelReachedException;

/**
 * 펫 레벨 Value Object
 */
public record PetLevel(int value) {
    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 10;

    public PetLevel {
        if (value < MIN_LEVEL || value > MAX_LEVEL) {
            throw PetInvalidValueException.invalidLevel(value);
        }
    }

    public static PetLevel of(int level) {
        return new PetLevel(level);
    }

    public static PetLevel initial() {
        return new PetLevel(MIN_LEVEL);
    }

    public boolean isMaxLevel() {
        return value == MAX_LEVEL;
    }

    public PetLevel levelUp() {
        if (isMaxLevel()) {
            throw new PetMaxLevelReachedException(value, MAX_LEVEL);
        }
        return new PetLevel(value + 1);
    }

    public boolean canLevelUp(Experience experience, Experience requiredExp) {
        return !isMaxLevel() && experience.isGreaterThanOrEqual(requiredExp);
    }
}