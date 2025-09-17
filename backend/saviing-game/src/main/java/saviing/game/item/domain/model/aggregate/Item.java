package saviing.game.item.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.item.domain.event.DomainEvent;
import saviing.game.item.domain.event.ItemAvailabilityChangedEvent;
import saviing.game.item.domain.event.ItemRegisteredEvent;
import saviing.game.item.domain.event.ItemUpdatedEvent;
import saviing.game.item.domain.exception.InvalidItemDefinitionException;
import saviing.game.item.domain.exception.ItemUnavailableException;
import saviing.game.item.domain.model.enums.category.Category;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.item.domain.model.vo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 아이템 Aggregate Root
 * 게임 아이템의 전체 생명주기와 비즈니스 규칙을 관리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    private ItemId itemId;
    private ItemName itemName;
    private ItemDescription itemDescription;
    private ItemType itemType;
    private Category itemCategory;
    private Rarity rarity;
    private ItemSize itemSize;
    private Price price;
    private ImageUrl imageUrl;
    private boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Item 생성자 (Builder 패턴 사용)
     */
    @Builder
    private Item(
        ItemId itemId,
        ItemName itemName,
        ItemDescription itemDescription,
        ItemType itemType,
        Category itemCategory,
        Rarity rarity,
        ItemSize itemSize,
        Price price,
        ImageUrl imageUrl,
        boolean isAvailable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription != null ? itemDescription : ItemDescription.empty();
        this.itemType = itemType;
        this.itemCategory = itemCategory;
        this.rarity = rarity != null ? rarity : Rarity.COMMON;
        this.itemSize = itemSize;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();

        validateInvariants();
    }

    /**
     * 새로운 아이템을 생성합니다.
     *
     * @param itemName 아이템 이름
     * @param itemDescription 아이템 설명
     * @param itemType 아이템 타입
     * @param itemCategory 아이템 카테고리
     * @param rarity 희귀도
     * @param itemSize 아이템 크기
     * @param price 가격
     * @param imageUrl 이미지 URL
     * @return 생성된 아이템
     */
    public static Item create(
        ItemName itemName,
        ItemDescription itemDescription,
        ItemType itemType,
        Category itemCategory,
        Rarity rarity,
        ItemSize itemSize,
        Price price,
        ImageUrl imageUrl
    ) {
        // 카테고리와 타입 일치성 검증
        if (!itemType.isValidCategory(itemCategory)) {
            throw InvalidItemDefinitionException.categoryTypeMismatch(
                itemCategory.getDisplayName(),
                itemType.getDisplayName()
            );
        }

        Item item = Item.builder()
            .itemName(itemName)
            .itemDescription(itemDescription)
            .itemType(itemType)
            .itemCategory(itemCategory)
            .rarity(rarity)
            .itemSize(itemSize)
            .price(price)
            .imageUrl(imageUrl)
            .isAvailable(true) // 기본적으로 판매 가능 상태로 생성
            .build();

        item.addDomainEvent(ItemRegisteredEvent.of(
            item.itemId,
            item.itemName,
            item.itemType,
            item.itemCategory
        ));

        return item;
    }

    /**
     * 아이템을 판매 가능 상태로 변경합니다.
     *
     * @throws ItemUnavailableException 이미 판매 가능한 경우
     */
    public void makeAvailable() {
        if (isAvailable) {
            throw ItemUnavailableException.alreadyAvailable(this.itemId);
        }

        this.isAvailable = true;
        updateTimestamp();

        addDomainEvent(ItemAvailabilityChangedEvent.makeAvailable(
            this.itemId,
            this.itemName
        ));
    }

    /**
     * 아이템을 판매 불가능 상태로 변경합니다.
     *
     * @param reason 판매 중단 사유
     * @throws ItemUnavailableException 이미 판매 불가능한 경우
     */
    public void makeUnavailable(String reason) {
        if (!isAvailable) {
            throw ItemUnavailableException.alreadyUnavailable(this.itemId);
        }

        this.isAvailable = false;
        updateTimestamp();

        addDomainEvent(ItemAvailabilityChangedEvent.makeUnavailable(
            this.itemId,
            this.itemName,
            reason
        ));
    }

    /**
     * 아이템 이름을 변경합니다.
     *
     * @param newName 새로운 아이템 이름
     */
    public void updateName(ItemName newName) {
        if (!this.itemName.equals(newName)) {
            String oldName = this.itemName.value();
            this.itemName = newName;
            updateTimestamp();

            addDomainEvent(ItemUpdatedEvent.ofSingleField(
                this.itemId,
                this.itemName,
                "name",
                Map.of("old", oldName, "new", newName.value())
            ));
        }
    }

    /**
     * 아이템 설명을 변경합니다.
     *
     * @param newDescription 새로운 아이템 설명
     */
    public void updateDescription(ItemDescription newDescription) {
        if (!this.itemDescription.equals(newDescription)) {
            String oldDescription = this.itemDescription.value();
            this.itemDescription = newDescription;
            updateTimestamp();

            addDomainEvent(ItemUpdatedEvent.ofSingleField(
                this.itemId,
                this.itemName,
                "description",
                Map.of("old", oldDescription, "new", newDescription.value())
            ));
        }
    }

    /**
     * 아이템 가격을 변경합니다.
     *
     * @param newPrice 새로운 가격
     */
    public void updatePrice(Price newPrice) {
        if (!this.price.equals(newPrice)) {
            Price oldPrice = this.price;
            this.price = newPrice;
            updateTimestamp();

            // 가격 변경은 비즈니스적으로 중요한 이벤트이므로 유지
            addDomainEvent(ItemUpdatedEvent.ofSingleField(
                this.itemId,
                this.itemName,
                "price",
                Map.of("old", oldPrice, "new", newPrice)
            ));
        }
    }

    /**
     * 아이템 이미지 URL을 변경합니다.
     *
     * @param newImageUrl 새로운 이미지 URL
     */
    public void updateImageUrl(ImageUrl newImageUrl) {
        if (!this.imageUrl.equals(newImageUrl)) {
            String oldUrl = this.imageUrl.value();
            this.imageUrl = newImageUrl;
            updateTimestamp();

            addDomainEvent(ItemUpdatedEvent.ofSingleField(
                this.itemId,
                this.itemName,
                "imageUrl",
                Map.of("old", oldUrl, "new", newImageUrl.value())
            ));
        }
    }

    /**
     * 아이템 희귀도를 변경합니다.
     *
     * @param newRarity 새로운 희귀도
     */
    public void updateRarity(Rarity newRarity) {
        if (!this.rarity.equals(newRarity)) {
            Rarity oldRarity = this.rarity;
            this.rarity = newRarity;
            updateTimestamp();

            addDomainEvent(ItemUpdatedEvent.ofSingleField(
                this.itemId,
                this.itemName,
                "rarity",
                Map.of("old", oldRarity.name(), "new", newRarity.name())
            ));
        }
    }



    /**
     * 아이템이 무료인지 확인합니다.
     *
     * @return 무료인지 여부
     */
    public boolean isFree() {
        return price.isFree();
    }

    /**
     * 아이템이 기본 크기(1x1)인지 확인합니다.
     *
     * @return 기본 크기인지 여부
     */
    public boolean isDefaultSize() {
        return itemSize != null && itemSize.isDefaultSize();
    }

    /**
     * 수정 시간을 현재 시간으로 업데이트합니다.
     */
    private void updateTimestamp() {
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
    private void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * 아이템 불변 조건을 검증합니다.
     *
     * @throws IllegalArgumentException 필수 필드가 null이거나 잘못된 경우
     */
    private void validateInvariants() {
        if (itemName == null) {
            throw new IllegalArgumentException("아이템 이름은 null일 수 없습니다");
        }
        if (itemType == null) {
            throw new IllegalArgumentException("아이템 타입은 null일 수 없습니다");
        }
        if (itemCategory == null) {
            throw new IllegalArgumentException("아이템 카테고리는 null일 수 없습니다");
        }
        if (price == null) {
            throw new IllegalArgumentException("아이템 가격은 null일 수 없습니다");
        }
        if (imageUrl == null) {
            throw new IllegalArgumentException("이미지 URL은 null일 수 없습니다");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("생성 시간은 null일 수 없습니다");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("수정 시간은 null일 수 없습니다");
        }

        // 카테고리와 타입 일치성 검증
        if (!itemType.isValidCategory(itemCategory)) {
            throw InvalidItemDefinitionException.categoryTypeMismatch(
                itemCategory.getDisplayName(),
                itemType.getDisplayName()
            );
        }

        // 데코레이션 아이템은 x,y 좌표가 필수
        if (itemType == ItemType.DECORATION) {
            if (itemSize == null || !itemSize.isDefined()) {
                throw InvalidItemDefinitionException.decorationSizeRequired();
            }
        }
        // PET, ACCESSORY 타입은 itemSize가 null이어도 허용
    }
}