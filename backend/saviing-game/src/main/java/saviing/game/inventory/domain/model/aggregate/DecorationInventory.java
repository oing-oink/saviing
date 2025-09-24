package saviing.game.inventory.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.event.InventoryItemAddedEvent;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.item.domain.model.enums.Decoration;
import saviing.game.item.domain.model.vo.ItemId;

import java.time.LocalDateTime;

/**
 * 데코레이션 인벤토리 Aggregate.
 * 데코레이션 아이템의 사용 여부와 배치된 방 정보를 관리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DecorationInventory extends Inventory {
    private Decoration category;
    private Long roomId;

    /**
     * DecorationInventory 생성자(Builder 패턴 사용).
     */
    @Builder
    private DecorationInventory(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        ItemId itemId,
        Long roomId,
        boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Decoration category
    ) {
        super(inventoryItemId, characterId, itemId, InventoryType.DECORATION, isUsed, createdAt, updatedAt);
        this.category = category;
        this.roomId = roomId;

        validateDecorationSpecificInvariants();
    }

    /**
     * 신규 데코레이션 인벤토리를 생성합니다.
     */
    public static DecorationInventory create(
        CharacterId characterId,
        ItemId itemId,
        Decoration decorationCategory
    ) {
        validateDecorationCategory(decorationCategory);

        DecorationInventory decorationInventory = DecorationInventory.builder()
            .characterId(characterId)
            .itemId(itemId)
            .category(decorationCategory)
            .isUsed(false)
            .build();

        decorationInventory.addDomainEvent(InventoryItemAddedEvent.of(
            decorationInventory.getInventoryItemId(),
            characterId,
            itemId,
            InventoryType.DECORATION
        ));

        return decorationInventory;
    }

    /**
     * 데코레이션을 특정 방에 배치합니다.
     *
     * @param roomId 배치할 방의 식별자
     * @throws IllegalStateException 이미 배치된 데코레이션인 경우
     * @throws IllegalArgumentException roomId가 null이거나 0 이하인 경우
     */
    public void placeInRoom(Long roomId) {
        if (roomId == null || roomId <= 0) {
            throw new IllegalArgumentException("roomId는 필수이며 양수여야 합니다");
        }
        if (this.isUsed) {
            throw new IllegalStateException("이미 배치된 데코레이션입니다");
        }

        this.roomId = roomId;
        use();
    }

    /**
     * 데코레이션을 방에서 제거합니다.
     *
     * @throws IllegalStateException 배치되지 않은 데코레이션인 경우
     */
    public void removeFromRoom() {
        if (!this.isUsed) {
            throw new IllegalStateException("배치되지 않은 데코레이션입니다");
        }

        this.roomId = null;
        unuse();
    }

    /**
     * 특정 방에 배치되어 있는지 확인합니다.
     *
     * @param roomId 확인할 방의 식별자
     * @return 해당 방에 배치되어 있으면 true, 그렇지 않으면 false
     */
    public boolean isPlacedInRoom(Long roomId) {
        return this.isUsed && roomId != null && roomId.equals(this.roomId);
    }

    /**
     * 지정한 카테고리와 동일한지 확인합니다.
     */
    public boolean isSameCategory(Decoration decorationCategory) {
        if (decorationCategory == null) {
            return false;
        }
        return this.category == decorationCategory;
    }


    public boolean isRoomColor() {
        return this.category == Decoration.ROOM_COLOR;
    }

    private static void validateDecorationCategory(Decoration decorationCategory) {
        if (decorationCategory == null) {
            throw new IllegalArgumentException("데코레이션 카테고리는 null일 수 없습니다");
        }
        // 모든 데코레이션 타입을 허용합니다.
    }

    private void validateDecorationSpecificInvariants() {
        if (category == null) {
            throw new IllegalArgumentException("데코레이션 카테고리는 null일 수 없습니다");
        }
    }

}