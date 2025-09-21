package saviing.game.inventory.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.common.event.DomainEvent;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.item.domain.model.vo.ItemId;

/**
 * 인벤토리 Aggregate Root (슈퍼 클래스)
 * 모든 인벤토리 아이템의 공통 속성과 기본 비즈니스 규칙을 관리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Inventory {
    protected InventoryItemId inventoryItemId;
    protected CharacterId characterId;
    protected ItemId itemId;
    protected InventoryType type;
    protected boolean isUsed;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Inventory 기본 생성자
     * 서브클래스에서 호출됩니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @param type 인벤토리 타입
     * @param isUsed 사용 여부
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     */
    protected Inventory(
        InventoryItemId inventoryItemId,
        CharacterId characterId,
        ItemId itemId,
        InventoryType type,
        boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.inventoryItemId = inventoryItemId != null ? inventoryItemId : InventoryItemId.newInstance();
        this.characterId = characterId;
        this.itemId = itemId;
        this.type = type;
        this.isUsed = isUsed;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();

        validateInvariants();
    }

    /**
     * 아이템을 사용 상태로 변경합니다.
     */
    public void use() {
        if (this.isUsed) {
            throw new IllegalStateException("이미 사용 중인 아이템입니다");
        }
        this.isUsed = true;
        updateTimestamp();
    }

    /**
     * 아이템 사용을 해제합니다.
     */
    public void unuse() {
        if (!this.isUsed) {
            throw new IllegalStateException("사용 중이지 않은 아이템입니다");
        }
        this.isUsed = false;
        updateTimestamp();
    }

    /**
     * 특정 캐릭터가 소유한 아이템인지 확인합니다.
     *
     * @param characterId 확인할 캐릭터 ID
     * @return 소유 여부
     */
    public boolean isOwnedBy(CharacterId characterId) {
        return this.characterId.equals(characterId);
    }

    /**
     * 아이템이 사용 가능한 상태인지 확인합니다.
     * 서브클래스에서 오버라이드하여 추가 조건을 검사할 수 있습니다.
     *
     * @return 사용 가능 여부
     */
    public boolean canUse() {
        return !isUsed;
    }

    /**
     * 수정 시간을 현재 시간으로 업데이트합니다.
     */
    protected void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 발행된 도메인 이벤트 목록을 반환합니다.
     *
     * @return 도메인 이벤트 목록 (읽기 전용)
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * 도메인 이벤트를 모두 삭제합니다.
     * 이벤트 발행 후 호출되어야 합니다.
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }

    /**
     * 도메인 이벤트를 추가합니다.
     *
     * @param event 추가할 도메인 이벤트
     */
    protected void addDomainEvent(DomainEvent event) {
        if (event != null) {
            domainEvents.add(event);
        }
    }

    /**
     * 인벤토리 불변 조건을 검증합니다.
     *
     * @throws IllegalArgumentException 필수 필드가 null이거나 잘못된 경우
     */
    private void validateInvariants() {
        if (characterId == null) {
            throw new IllegalArgumentException("캐릭터 ID는 null일 수 없습니다");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("아이템 ID는 null일 수 없습니다");
        }
        if (type == null) {
            throw new IllegalArgumentException("인벤토리 타입은 null일 수 없습니다");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("생성 시간은 null일 수 없습니다");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("수정 시간은 null일 수 없습니다");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inventory inventory = (Inventory) o;
        return inventoryItemId != null && inventoryItemId.equals(inventory.inventoryItemId);
    }

    @Override
    public int hashCode() {
        return inventoryItemId != null ? inventoryItemId.hashCode() : 0;
    }

}