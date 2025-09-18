package saviing.game.item.domain.repository;

import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.domain.model.enums.category.Category;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.item.domain.model.vo.ItemId;

import java.util.List;
import java.util.Optional;

/**
 * 아이템 도메인의 Repository 인터페이스
 * 아이템 Aggregate의 영속성을 담당합니다.
 */
public interface ItemRepository {

    /**
     * 아이템을 저장합니다.
     *
     * @param item 저장할 아이템
     * @return 저장된 아이템
     */
    Item save(Item item);

    /**
     * 아이템 ID로 아이템을 조회합니다.
     *
     * @param itemId 아이템 ID
     * @return 조회된 아이템 (Optional)
     */
    Optional<Item> findById(ItemId itemId);

    /**
     * 아이템이 존재하는지 확인합니다.
     *
     * @param itemId 아이템 ID
     * @return 아이템 존재 여부
     */
    boolean existsById(ItemId itemId);

    /**
     * 아이템을 삭제합니다.
     *
     * @param itemId 삭제할 아이템 ID
     */
    void deleteById(ItemId itemId);

    /**
     * 조건에 맞는 아이템을 DB에서 정렬/필터링하여 조회합니다.
     *
     * @param itemType 아이템 타입
     * @param category 아이템 카테고리
     * @param rarity 희귀도
     * @param keyword 이름 검색 키워드
     * @param available 판매 가능 여부
     * @param sortField 정렬 필드
     * @param sortDirection 정렬 방향
     * @param coinType 코인 타입
     * @return 조회된 아이템 목록
     */
    List<Item> findItemsWithConditions(
        ItemType itemType,
        Category category,
        Rarity rarity,
        String keyword,
        Boolean available,
        String sortField,
        String sortDirection,
        String coinType
    );
}