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
     * 아이템 타입으로 아이템 목록을 조회합니다.
     *
     * @param itemType 아이템 타입
     * @return 해당 타입의 아이템 목록
     */
    List<Item> findByType(ItemType itemType);

    /**
     * 아이템 카테고리로 아이템 목록을 조회합니다.
     *
     * @param category 아이템 카테고리
     * @return 해당 카테고리의 아이템 목록
     */
    List<Item> findByCategory(Category category);

    /**
     * 희귀도로 아이템 목록을 조회합니다.
     *
     * @param rarity 희귀도
     * @return 해당 희귀도의 아이템 목록
     */
    List<Item> findByRarity(Rarity rarity);

    /**
     * 판매 가능한 모든 아이템을 조회합니다.
     *
     * @return 판매 가능한 아이템 목록
     */
    List<Item> findAllAvailable();

    /**
     * 특정 타입의 판매 가능한 아이템을 조회합니다.
     *
     * @param itemType 아이템 타입
     * @return 해당 타입의 판매 가능한 아이템 목록
     */
    List<Item> findAvailableByType(ItemType itemType);

    /**
     * 특정 카테고리의 판매 가능한 아이템을 조회합니다.
     *
     * @param category 아이템 카테고리
     * @return 해당 카테고리의 판매 가능한 아이템 목록
     */
    List<Item> findAvailableByCategory(Category category);

    /**
     * 모든 아이템을 조회합니다 (판매 불가능한 아이템 포함).
     *
     * @return 모든 아이템 목록
     */
    List<Item> findAll();

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
     * 아이템 이름으로 검색합니다 (부분 검색 지원).
     *
     * @param keyword 검색 키워드
     * @return 이름에 키워드가 포함된 아이템 목록
     */
    List<Item> findByNameContaining(String keyword);
}