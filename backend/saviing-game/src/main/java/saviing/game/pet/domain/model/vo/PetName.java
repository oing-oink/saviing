package saviing.game.pet.domain.model.vo;

/**
 * 펫 이름 Value Object
 */
public record PetName(String value) {
    private static final int MAX_LENGTH = 20;

    public PetName {
        if (value == null) {
            throw new IllegalArgumentException("펫 이름은 null일 수 없습니다");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("펫 이름은 빈 문자열일 수 없습니다");
        }
        if (value.trim().length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("펫 이름은 %d자를 초과할 수 없습니다. 입력값: %s", MAX_LENGTH, value)
            );
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