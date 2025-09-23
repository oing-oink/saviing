package saviing.game.room.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import saviing.game.room.domain.model.vo.RoomId;

/**
 * 방 내 전체 아이템 배치를 관리하는 애그리거트 루트
 * 룸별로 하나씩 존재하며, 해당 룸 내 모든 배치된 아이템들을 관리함
 * 배치 규칙 검증 및 트랜잭션 일관성을 보장함
 */
@Getter
public class Placement {

    private final RoomId roomId;
    private final List<PlacedItem> placedItems;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private static final int MAX_PET_COUNT = 2;

    /**
     * Placement 생성자 (내부용)
     *
     * @param roomId 방 식별자
     * @param placedItems 배치된 아이템 목록
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     */
    private Placement(RoomId roomId, List<PlacedItem> placedItems,
            LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.roomId = roomId;
        this.placedItems = new ArrayList<>(placedItems);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 새로운 룸 배치를 생성하는 정적 팩토리 메서드
     *
     * @param roomId 방 식별자
     * @return 새로 생성된 Placement 인스턴스 (빈 배치 목록으로 시작)
     * @throws IllegalArgumentException roomId가 null인 경우
     */
    public static Placement create(@NonNull RoomId roomId) {
        LocalDateTime now = LocalDateTime.now();
        return new Placement(roomId, Collections.emptyList(), now, now);
    }

    /**
     * 기존 배치를 복원하는 정적 팩토리 메서드 (영속성 계층용)
     *
     * @param roomId 방 식별자
     * @param placedItems 배치된 아이템 목록
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 Placement 인스턴스
     * @throws IllegalArgumentException 필수 파라미터가 null인 경우
     */
    public static Placement restore(
            @NonNull RoomId roomId,
            @NonNull List<PlacedItem> placedItems,
            @NonNull LocalDateTime createdAt,
            @NonNull LocalDateTime updatedAt
    ) {
        return new Placement(roomId, placedItems, createdAt, updatedAt);
    }

    /**
     * 아이템을 배치에 추가
     *
     * @param item 추가할 배치 아이템
     * @throws IllegalArgumentException item이 null이거나 배치 규칙을 위반하는 경우
     */
    public void addItem(@NonNull PlacedItem item) {

        validatePlacement(item);
        List<PlacedItem> newItems = new ArrayList<>(this.placedItems);
        newItems.add(item);

        validateAllItems(newItems);

        this.placedItems.add(item);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 인벤토리 아이템 ID로 배치에서 제거
     *
     * @param inventoryItemId 제거할 인벤토리 아이템 식별자
     * @return 제거 성공 여부
     */
    public boolean removeItem(Long inventoryItemId) {
        Objects.requireNonNull(inventoryItemId, "inventoryItemId");

        boolean removed = this.placedItems.removeIf(item ->
            item.getInventoryItemId().equals(inventoryItemId));

        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }

        return removed;
    }

    /**
     * 모든 배치 아이템을 새로운 목록으로 교체
     *
     * @param items 새로운 배치 아이템 목록
     * @throws IllegalArgumentException items가 null이거나 배치 규칙을 위반하는 경우
     */
    public void replaceAllItems(List<PlacedItem> items) {
        Objects.requireNonNull(items, "items");

        List<PlacedItem> newItems = new ArrayList<>(items);
        validateAllItems(newItems);

        this.placedItems.clear();
        this.placedItems.addAll(newItems);
        this.updatedAt = LocalDateTime.now();

    }

    /**
     * 배치된 아이템 목록을 읽기 전용으로 반환
     *
     * @return 배치된 아이템 목록 (수정 불가)
     */
    public List<PlacedItem> getPlacedItems() {
        return Collections.unmodifiableList(placedItems);
    }

    /**
     * 현재 배치된 아이템 개수 반환
     *
     * @return 배치된 아이템 개수
     */
    public int getItemCount() {
        return placedItems.size();
    }

    /**
     * 특정 인벤토리 아이템이 배치되어 있는지 확인
     *
     * @param inventoryItemId 확인할 인벤토리 아이템 식별자
     * @return 배치되어 있으면 true, 그렇지 않으면 false
     */
    public boolean hasItem(Long inventoryItemId) {
        return placedItems.stream()
            .anyMatch(item -> item.getInventoryItemId().equals(inventoryItemId));
    }

    /**
     * 개별 배치 아이템 검증
     *
     * @param item 검증할 배치 아이템
     * @throws IllegalArgumentException 검증 실패 시
     */
    private void validatePlacement(@NonNull PlacedItem item) {
        if (hasItem(item.getInventoryItemId())) {
            throw new IllegalArgumentException(
                "Duplicate inventory item placement: " + item.getInventoryItemId());
        }
    }

    /**
     * 전체 배치 아이템 목록 검증
     *
     * @param items 검증할 배치 아이템 목록
     * @throws IllegalArgumentException 검증 실패 시
     */
    private void validateAllItems(List<PlacedItem> items) {
        validateNoDuplicateItems(items);
        validateNoOverlaps(items);
        validatePetCount(items);
    }

    /**
     * 중복 아이템 검증
     *
     * @param items 검증할 배치 아이템 목록
     * @throws IllegalArgumentException 중복 아이템이 있는 경우
     */
    private void validateNoDuplicateItems(List<PlacedItem> items) {
        Set<Long> inventoryItemIds = new HashSet<>();
        for (PlacedItem item : items) {
            if (!inventoryItemIds.add(item.getInventoryItemId())) {
                throw new IllegalArgumentException(
                    "Duplicate inventory item placement: " + item.getInventoryItemId());
            }
        }
    }

    /**
     * 배치 겹침 검증
     *
     * @param items 검증할 배치 아이템 목록
     * @throws IllegalArgumentException 겹치는 배치가 있는 경우
     */
    private void validateNoOverlaps(List<PlacedItem> items) {
        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                PlacedItem item1 = items.get(i);
                PlacedItem item2 = items.get(j);

                if (item1.overlaps(item2)) {
                    throw new IllegalArgumentException(
                        String.format("Placements overlap: item %d and item %d",
                            item1.getInventoryItemId(), item2.getInventoryItemId()));
                }
            }
        }
    }

    /**
     * 펫 개수 제한 검증
     *
     * @param items 검증할 배치 아이템 목록
     * @throws IllegalArgumentException 펫 개수가 제한을 초과하는 경우
     */
    private void validatePetCount(List<PlacedItem> items) {
        long petCount = items.stream()
            .filter(PlacedItem::isPet)
            .count();

        if (petCount > MAX_PET_COUNT) {
            throw new IllegalArgumentException(
                "Cannot place more than " + MAX_PET_COUNT + " pets. Found: " + petCount);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Placement placement = (Placement) o;
        return Objects.equals(roomId, placement.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }


}
