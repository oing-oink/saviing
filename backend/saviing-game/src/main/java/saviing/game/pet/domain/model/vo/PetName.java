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
        if (value.trim().length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("펫 이름은 %d자를 초과할 수 없습니다. 입력값: %s", MAX_LENGTH, value)
            );
        }
    }

    public static PetName of(String name) {
        return new PetName(name);
    }

    public static PetName empty() {
        return new PetName("");
    }

    /**
     * 아이템 이름으로 펫 이름을 생성합니다.
     * 펫 생성 시 기본 이름으로 사용됩니다.
     */
    public static PetName fromItemName(String itemName) {
        if (itemName == null) {
            throw new IllegalArgumentException("아이템 이름은 null일 수 없습니다");
        }
        if (itemName.trim().isEmpty()) {
            return empty();
        }
        return new PetName(itemName.trim());
    }

    public boolean isEmpty() {
        return value.trim().isEmpty();
    }

    public String displayName() {
        return isEmpty() ? "이름 없음" : value.trim();
    }
}