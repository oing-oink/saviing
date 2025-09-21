package saviing.game.inventory.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.event.InventoryItemAddedEvent;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.item.domain.model.enums.Pet;
import saviing.game.item.domain.model.vo.ItemId;

import java.time.LocalDateTime;

/**
 * 펫 인벤토리 Aggregate.
 * 펫 아이템의 소유권과 사용 여부만 관리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetInventory extends Inventory {
    private Pet category;
    private Long roomId;

    /**
     * PetInventory 생성자(Builder 패턴 사용).
     */
    @Builder
    private PetInventory(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        ItemId itemId,
        boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Pet category,
        Long roomId
    ) {
        super(inventoryItemId, characterId, itemId, InventoryType.PET, isUsed, createdAt, updatedAt);
        this.category = category;
        this.roomId = roomId;

        validatePetSpecificInvariants();
    }

    /**
     * 신규 펫 인벤토리를 생성합니다.
     */
    public static PetInventory create(
        CharacterId characterId,
        ItemId itemId,
        Pet petCategory
    ) {
        validatePetCategory(petCategory);

        PetInventory petInventory = PetInventory.builder()
            .characterId(characterId)
            .itemId(itemId)
            .category(petCategory)
            .roomId(null)
            .isUsed(false)
            .build();

        petInventory.addDomainEvent(InventoryItemAddedEvent.of(
            petInventory.getInventoryItemId(),
            characterId,
            itemId,
            InventoryType.PET
        ));

        return petInventory;
    }

    /**
     * 펫을 특정 방에 배치합니다.
     */
    public void placeInRoom(Long roomId) {
        this.roomId = roomId;
        updateTimestamp();
    }

    private static void validatePetCategory(Pet petCategory) {
        if (petCategory == null) {
            throw new IllegalArgumentException("펫 카테고리는 null일 수 없습니다");
        }
        // 현재는 CAT만 지원합니다.
        if (petCategory != Pet.CAT) {
            throw new IllegalArgumentException("지원하지 않는 펫 카테고리입니다: " + petCategory);
        }
    }

    private void validatePetSpecificInvariants() {
        if (category == null) {
            throw new IllegalArgumentException("펫 카테고리는 null일 수 없습니다");
        }
    }

    /**
     * 펫 카테고리가 일치하는지 확인합니다.
     */
    public boolean isCategory(Pet petCategory) {
        return this.category == petCategory;
    }

}