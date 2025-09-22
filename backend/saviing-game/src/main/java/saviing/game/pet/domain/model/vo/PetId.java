package saviing.game.pet.domain.model.vo;

/**
 * 펫 식별자 Value Object
 * pet 테이블의 inventory_item_id와 매핑됩니다.
 */
public record PetId(Long value) {
    public PetId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("펫 ID는 양수여야 합니다");
        }
    }

    public static PetId of(Long value) {
        return new PetId(value);
    }
}