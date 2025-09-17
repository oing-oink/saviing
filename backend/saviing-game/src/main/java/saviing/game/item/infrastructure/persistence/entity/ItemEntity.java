package saviing.game.item.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 아이템 JPA 엔티티
 * item 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "item_description", length = 500)
    private String itemDescription;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType;

    @Column(name = "item_category", nullable = false, length = 30)
    private String itemCategory;

    @Column(name = "rarity", nullable = false, length = 20)
    private String rarity;

    @Column(name = "x_length")
    private Integer xLength;

    @Column(name = "y_length")
    private Integer yLength;

    @Column(name = "coin", nullable = false)
    private Integer coin;

    @Column(name = "fish_coin")
    private Integer fishCoin;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ItemEntity(
        Long itemId,
        String itemName,
        String itemDescription,
        String itemType,
        String itemCategory,
        String rarity,
        Integer xLength,
        Integer yLength,
        Integer coin,
        Integer fishCoin,
        String imageUrl,
        Boolean isAvailable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemType = itemType;
        this.itemCategory = itemCategory;
        this.rarity = rarity;
        this.xLength = xLength;
        this.yLength = yLength;
        this.coin = coin;
        this.fishCoin = fishCoin;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 엔티티 업데이트 시 호출됩니다.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티 생성 시 호출됩니다.
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    /**
     * 아이템 정보를 업데이트합니다.
     *
     * @param itemName 아이템 이름
     * @param itemDescription 아이템 설명
     * @param rarity 희귀도
     * @param coin 코인 가격
     * @param fishCoin 피쉬 코인 가격
     * @param imageUrl 이미지 URL
     */
    public void updateItem(
        String itemName,
        String itemDescription,
        String rarity,
        Integer coin,
        Integer fishCoin,
        String imageUrl
    ) {
        if (itemName != null) {
            this.itemName = itemName;
        }
        if (itemDescription != null) {
            this.itemDescription = itemDescription;
        }
        if (rarity != null) {
            this.rarity = rarity;
        }
        if (coin != null) {
            this.coin = coin;
        }
        if (fishCoin != null) {
            this.fishCoin = fishCoin;
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 아이템 가용성을 변경합니다.
     *
     * @param isAvailable 가용성 여부
     */
    public void changeAvailability(Boolean isAvailable) {
        this.isAvailable = isAvailable;
        this.updatedAt = LocalDateTime.now();
    }
}