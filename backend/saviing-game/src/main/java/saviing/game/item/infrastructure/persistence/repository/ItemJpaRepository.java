package saviing.game.item.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import saviing.game.item.infrastructure.persistence.entity.ItemEntity;

import java.util.List;

/**
 * 아이템 JPA Repository
 * Spring Data JPA를 사용한 기본 CRUD 및 쿼리 메서드를 제공합니다.
 */
public interface ItemJpaRepository extends JpaRepository<ItemEntity, Long> {

    /**
     * 아이템 타입으로 아이템 목록을 조회합니다.
     *
     * @param itemType 아이템 타입
     * @return 해당 타입의 아이템 목록
     */
    List<ItemEntity> findByItemType(String itemType);

    /**
     * 아이템 카테고리로 아이템 목록을 조회합니다.
     *
     * @param itemCategory 아이템 카테고리
     * @return 해당 카테고리의 아이템 목록
     */
    List<ItemEntity> findByItemCategory(String itemCategory);

    /**
     * 희귀도로 아이템 목록을 조회합니다.
     *
     * @param rarity 희귀도
     * @return 해당 희귀도의 아이템 목록
     */
    List<ItemEntity> findByRarity(String rarity);

    /**
     * 판매 가능한 모든 아이템을 조회합니다.
     *
     * @return 판매 가능한 아이템 목록
     */
    List<ItemEntity> findByIsAvailableTrue();

    /**
     * 특정 타입의 판매 가능한 아이템을 조회합니다.
     *
     * @param itemType 아이템 타입
     * @return 해당 타입의 판매 가능한 아이템 목록
     */
    List<ItemEntity> findByItemTypeAndIsAvailableTrue(String itemType);

    /**
     * 특정 카테고리의 판매 가능한 아이템을 조회합니다.
     *
     * @param itemCategory 아이템 카테고리
     * @return 해당 카테고리의 판매 가능한 아이템 목록
     */
    List<ItemEntity> findByItemCategoryAndIsAvailableTrue(String itemCategory);


    /**
     * 아이템 이름으로 검색합니다 (부분 검색 지원).
     *
     * @param keyword 검색 키워드
     * @return 이름에 키워드가 포함된 아이템 목록
     */
    List<ItemEntity> findByItemNameContainingIgnoreCase(String keyword);

    /**
     * 판매 가능한 아이템을 이름 순으로 정렬하여 조회합니다.
     *
     * @return 판매 가능한 아이템 목록 (이름 순)
     */
    List<ItemEntity> findByIsAvailableTrueOrderByItemNameAsc();

    /**
     * 판매 가능한 아이템을 코인 가격 순으로 정렬하여 조회합니다.
     *
     * @return 판매 가능한 아이템 목록 (코인 가격 순)
     */
    List<ItemEntity> findByIsAvailableTrueOrderByCoinAsc();

    /**
     * 판매 가능한 아이템을 피쉬 코인 가격 순으로 정렬하여 조회합니다.
     *
     * @return 판매 가능한 아이템 목록 (피쉬 코인 가격 순)
     */
    List<ItemEntity> findByIsAvailableTrueOrderByFishCoinAsc();

    /**
     * 판매 가능한 아이템을 희귀도 순으로 정렬하여 조회합니다.
     *
     * @return 판매 가능한 아이템 목록 (희귀도 순)
     */
    List<ItemEntity> findByIsAvailableTrueOrderByRarityAsc();

    /**
     * 판매 가능한 아이템을 생성일 순으로 정렬하여 조회합니다.
     *
     * @return 판매 가능한 아이템 목록 (생성일 순)
     */
    List<ItemEntity> findByIsAvailableTrueOrderByCreatedAtDesc();
}