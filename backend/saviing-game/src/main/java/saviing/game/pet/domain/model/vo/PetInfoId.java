package saviing.game.pet.domain.model.vo;

/**
 * 펫 정보 식별자 Value Object
 * pet_info 테이블의 inventory_item_id와 매핑됩니다.
 */
public record PetInfoId(Long value) {
    public PetInfoId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("펫 정보 ID는 양수여야 합니다");
        }
    }

    public static PetInfoId of(Long value) {
        return new PetInfoId(value);
    }
}