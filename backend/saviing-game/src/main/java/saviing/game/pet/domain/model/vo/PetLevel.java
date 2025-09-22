package saviing.game.pet.domain.model.vo;

/**
 * 펫 레벨 Value Object
 */
public record PetLevel(int value) {
    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 10;

    public PetLevel {
        if (value < MIN_LEVEL || value > MAX_LEVEL) {
            throw new IllegalArgumentException(
                String.format("펫 레벨은 %d와 %d 사이여야 합니다. 입력값: %d", MIN_LEVEL, MAX_LEVEL, value)
            );
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
            throw new IllegalStateException("이미 최대 레벨입니다");
        }
        return new PetLevel(value + 1);
    }

    public boolean canLevelUp(Experience experience, Experience requiredExp) {
        return !isMaxLevel() && experience.isGreaterThanOrEqual(requiredExp);
    }
}