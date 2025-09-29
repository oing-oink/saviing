package saviing.game.pet.domain.model.vo;

import saviing.game.pet.domain.exception.PetInvalidValueException;
import saviing.game.pet.domain.exception.PetInvalidAmountException;

/**
 * 펫 포만감 Value Object
 * 활동 시 소모되고 시간에 따라 회복되는 시스템
 */
public record Energy(int value) {
    private static final int MIN_ENERGY = 0;
    private static final int MAX_ENERGY = 100;
    private static final int INITIAL_ENERGY = 100;

    public Energy {
        if (value < MIN_ENERGY || value > MAX_ENERGY) {
            throw PetInvalidValueException.invalidEnergy(value);
        }
    }

    public static Energy of(int energy) {
        return new Energy(energy);
    }

    public static Energy initial() {
        return new Energy(INITIAL_ENERGY);
    }

    public static Energy max() {
        return new Energy(MAX_ENERGY);
    }

    public static Energy min() {
        return new Energy(MIN_ENERGY);
    }

    public Energy consume(Energy amount) {
        if (amount.value < 0) {
            throw PetInvalidAmountException.invalidEnergyAmount(amount.value, "소모");
        }
        int newValue = Math.max(MIN_ENERGY, value - amount.value);
        return new Energy(newValue);
    }

    public Energy recover(Energy amount) {
        if (amount.value < 0) {
            throw PetInvalidAmountException.invalidEnergyAmount(amount.value, "회복");
        }
        int newValue = Math.min(MAX_ENERGY, value + amount.value);
        return new Energy(newValue);
    }

    public boolean hasEnoughEnergy(Energy required) {
        return value >= required.value;
    }

    public boolean isMaxEnergy() {
        return value == MAX_ENERGY;
    }

    public boolean isMinEnergy() {
        return value == MIN_ENERGY;
    }

    /**
     * 포만감 범위를 반환합니다 (배수 계산용)
     * 0-25: LOW, 26-50: MEDIUM, 51-75: HIGH, 76-100: VERY_HIGH
     */
    public EnergyRange getRange() {
        if (value <= 25) return EnergyRange.LOW;
        if (value <= 50) return EnergyRange.MEDIUM;
        if (value <= 75) return EnergyRange.HIGH;
        return EnergyRange.VERY_HIGH;
    }

    public enum EnergyRange {
        LOW, MEDIUM, HIGH, VERY_HIGH
    }
}