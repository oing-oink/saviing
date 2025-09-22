package saviing.game.pet.domain.model.vo;

/**
 * 펫 경험치 Value Object
 */
public record Experience(int value) {
    private static final int MIN_EXPERIENCE = 0;

    public Experience {
        if (value < MIN_EXPERIENCE) {
            throw new IllegalArgumentException(
                String.format("펫 경험치는 %d 이상이어야 합니다. 입력값: %d", MIN_EXPERIENCE, value)
            );
        }
    }

    public static Experience of(int experience) {
        return new Experience(experience);
    }

    public static Experience initial() {
        return new Experience(MIN_EXPERIENCE);
    }

    public Experience add(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("경험치 증가량은 음수일 수 없습니다");
        }
        return new Experience(value + amount);
    }

    public boolean isGreaterThanOrEqual(int targetExp) {
        return value >= targetExp;
    }
}