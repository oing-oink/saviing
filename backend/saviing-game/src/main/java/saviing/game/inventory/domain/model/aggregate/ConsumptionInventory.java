package saviing.game.inventory.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.event.InventoryItemAddedEvent;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.item.domain.model.enums.Consumption;
import saviing.game.item.domain.model.vo.ItemId;

import java.time.LocalDateTime;

/**
 * 소모품 인벤토리 Aggregate.
 * 소모품 아이템의 개수를 관리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsumptionInventory extends Inventory {
    private Consumption category;
    private Integer count;

    /**
     * ConsumptionInventory 생성자(Builder 패턴 사용).
     */
    @Builder
    private ConsumptionInventory(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        ItemId itemId,
        boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Consumption category,
        Integer count
    ) {
        super(inventoryItemId, characterId, itemId, InventoryType.CONSUMPTION, isUsed, createdAt, updatedAt);
        this.category = category;
        this.count = count;

        validateConsumptionSpecificInvariants();
    }

    /**
     * 신규 소모품 인벤토리를 생성합니다.
     */
    public static ConsumptionInventory create(
        CharacterId characterId,
        ItemId itemId,
        Consumption consumptionCategory
    ) {
        validateConsumptionCategory(consumptionCategory);

        ConsumptionInventory consumptionInventory = ConsumptionInventory.builder()
            .characterId(characterId)
            .itemId(itemId)
            .category(consumptionCategory)
            .count(1)
            .isUsed(false)
            .build();

        consumptionInventory.addDomainEvent(InventoryItemAddedEvent.of(
            consumptionInventory.getInventoryItemId(),
            characterId,
            itemId,
            InventoryType.CONSUMPTION
        ));

        return consumptionInventory;
    }

    /**
     * 소모품 개수를 증가시킵니다.
     */
    public void increaseCount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("증가량은 1 이상이어야 합니다");
        }
        this.count += amount;
        updateTimestamp();
    }

    /**
     * 소모품 개수를 감소시킵니다.
     */
    public void decreaseCount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("감소량은 1 이상이어야 합니다");
        }
        if (this.count < amount) {
            throw new IllegalArgumentException("보유한 개수보다 많이 감소시킬 수 없습니다");
        }
        this.count -= amount;
        updateTimestamp();
    }

    private static void validateConsumptionCategory(Consumption consumptionCategory) {
        if (consumptionCategory == null) {
            throw new IllegalArgumentException("소모품 카테고리는 null일 수 없습니다");
        }
    }

    private void validateConsumptionSpecificInvariants() {
        if (category == null) {
            throw new IllegalArgumentException("소모품 카테고리는 null일 수 없습니다");
        }
        if (count == null || count < 0) {
            throw new IllegalArgumentException("소모품 개수는 0 이상이어야 합니다");
        }
    }

    /**
     * 소모품 카테고리가 일치하는지 확인합니다.
     */
    public boolean isCategory(Consumption consumptionCategory) {
        return this.category == consumptionCategory;
    }

    /**
     * 소모품이 소진되었는지 확인합니다.
     */
    public boolean isExhausted() {
        return this.count <= 0;
    }
}