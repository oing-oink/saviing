package saviing.game.inventory.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인벤토리 JPA 엔티티 (슈퍼클래스)
 * inventory 테이블과 매핑되며, 조인 상속 전략을 사용합니다.
 */
@Entity
@Table(name = "inventory")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_item_id")
    private Long inventoryItemId;

    @Column(name = "character_id", nullable = false)
    private Long characterId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private InventoryTypeEntity type;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * InventoryEntity 기본 생성자
     */
    protected InventoryEntity(
        Long inventoryItemId,
        Long characterId,
        Long itemId,
        InventoryTypeEntity type,
        Boolean isUsed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.inventoryItemId = inventoryItemId;
        this.characterId = characterId;
        this.itemId = itemId;
        this.type = type;
        this.isUsed = isUsed != null ? isUsed : false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 공통 필드를 업데이트합니다.
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @param type 인벤토리 타입
     * @param isUsed 사용 여부
     */
    public void updateCommonFields(
        Long characterId,
        Long itemId,
        InventoryTypeEntity type,
        Boolean isUsed
    ) {
        this.characterId = characterId;
        this.itemId = itemId;
        this.type = type;
        this.isUsed = isUsed;
    }

    /**
     * 사용 상태를 변경합니다.
     *
     * @param isUsed 사용 여부
     */
    public void changeUsedStatus(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    /**
     * 엔티티 저장 전 자동으로 호출되어 생성/수정 시간을 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 엔티티 수정 전 자동으로 호출되어 수정 시간을 업데이트합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 인벤토리 타입 열거형
     */
    public enum InventoryTypeEntity {
        PET, ACCESSORY, DECORATION, CONSUMPTION
    }
}