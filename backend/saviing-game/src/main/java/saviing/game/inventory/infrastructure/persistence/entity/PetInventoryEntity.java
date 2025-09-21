package saviing.game.inventory.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 펫 인벤토리 JPA 엔티티
 * pet_inventory 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "pet_inventory")
@PrimaryKeyJoinColumn(name = "inventory_item_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetInventoryEntity extends InventoryEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private PetCategoryEntity category;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "exp", nullable = false)
    private Integer exp;

    @Column(name = "affection", nullable = false)
    private Integer affection;

    @Column(name = "energy", nullable = false)
    private Integer energy;

    @Builder
    public PetInventoryEntity(
        Long inventoryItemId,
        Long characterId,
        Long itemId,
        Boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        PetCategoryEntity category,
        Long roomId,
        String name,
        Integer level,
        Integer exp,
        Integer affection,
        Integer energy
    ) {
        super(inventoryItemId, characterId, itemId, InventoryTypeEntity.PET, isUsed, createdAt, updatedAt);
        this.category = category;
        this.roomId = roomId;
        this.name = name;
        this.level = level != null ? level : 1;
        this.exp = exp != null ? exp : 0;
        this.affection = affection != null ? affection : 50;
        this.energy = energy != null ? energy : 100;
    }

    /**
     * 펫 정보를 업데이트합니다.
     *
     * @param category 펫 카테고리
     * @param roomId 방 ID
     * @param name 펫 이름
     * @param level 레벨
     * @param exp 경험치
     * @param affection 애정도
     * @param energy 에너지
     */
    public void updatePetInfo(
        PetCategoryEntity category,
        Long roomId,
        String name,
        Integer level,
        Integer exp,
        Integer affection,
        Integer energy
    ) {
        this.category = category;
        this.roomId = roomId;
        this.name = name;
        this.level = level;
        this.exp = exp;
        this.affection = affection;
        this.energy = energy;
    }

    /**
     * 펫 스탯을 업데이트합니다.
     *
     * @param level 레벨
     * @param exp 경험치
     * @param affection 애정도
     * @param energy 에너지
     */
    public void updatePetStats(Integer level, Integer exp, Integer affection, Integer energy) {
        this.level = level;
        this.exp = exp;
        this.affection = affection;
        this.energy = energy;
    }

    /**
     * 펫 이름을 변경합니다.
     *
     * @param name 새로운 펫 이름
     */
    public void changeName(String name) {
        this.name = name;
    }

    /**
     * 펫을 다른 방으로 이동합니다.
     *
     * @param roomId 이동할 방 ID
     */
    public void moveToRoom(Long roomId) {
        this.roomId = roomId;
    }

    /**
     * 펫 카테고리 열거형
     */
    public enum PetCategoryEntity {
        CAT
    }
}