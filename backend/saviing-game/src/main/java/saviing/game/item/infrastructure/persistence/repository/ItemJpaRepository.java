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
     * 동적 조건과 정렬을 적용하여 아이템을 조회합니다.
     * 모든 정렬 조합(12가지)을 단일 쿼리로 처리합니다.
     *
     * @param itemType 아이템 타입
     * @param category 아이템 카테고리
     * @param rarity 희귀도
     * @param keyword 이름 검색 키워드
     * @param available 판매 가능 여부
     * @param sortField 정렬 필드 (NAME, PRICE, RARITY, CREATED_AT, UPDATED_AT)
     * @param sortDirection 정렬 방향 (ASC, DESC)
     * @param coinType 코인 타입 (COIN, FISH_COIN) - PRICE 정렬시 필요
     * @return 조건에 맞는 아이템 목록
     */
    @Query("SELECT i FROM ItemEntity i WHERE " +
           "(:itemType IS NULL OR i.itemType = :itemType) AND " +
           "(:category IS NULL OR i.itemCategory = :category) AND " +
           "(:rarity IS NULL OR i.rarity = :rarity) AND " +
           "(:keyword IS NULL OR LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:available IS NULL OR i.isAvailable = :available) " +
           "ORDER BY " +
           "CASE WHEN :sortField = 'NAME' AND :sortDirection = 'ASC' THEN i.itemName END ASC, " +
           "CASE WHEN :sortField = 'NAME' AND :sortDirection = 'DESC' THEN i.itemName END DESC, " +
           "CASE WHEN :sortField = 'PRICE' AND :coinType = 'COIN' AND :sortDirection = 'ASC' THEN i.coin END ASC, " +
           "CASE WHEN :sortField = 'PRICE' AND :coinType = 'COIN' AND :sortDirection = 'DESC' THEN i.coin END DESC, " +
           "CASE WHEN :sortField = 'PRICE' AND :coinType = 'FISH_COIN' AND :sortDirection = 'ASC' THEN i.fishCoin END ASC, " +
           "CASE WHEN :sortField = 'PRICE' AND :coinType = 'FISH_COIN' AND :sortDirection = 'DESC' THEN i.fishCoin END DESC, " +
           "CASE WHEN :sortField = 'RARITY' AND :sortDirection = 'ASC' THEN i.rarity END ASC, " +
           "CASE WHEN :sortField = 'RARITY' AND :sortDirection = 'DESC' THEN i.rarity END DESC, " +
           "CASE WHEN :sortField = 'CREATED_AT' AND :sortDirection = 'ASC' THEN i.createdAt END ASC, " +
           "CASE WHEN :sortField = 'CREATED_AT' AND :sortDirection = 'DESC' THEN i.createdAt END DESC, " +
           "CASE WHEN :sortField = 'UPDATED_AT' AND :sortDirection = 'ASC' THEN i.updatedAt END ASC, " +
           "CASE WHEN :sortField = 'UPDATED_AT' AND :sortDirection = 'DESC' THEN i.updatedAt END DESC, " +
           "i.itemName ASC")
    List<ItemEntity> findItemsWithDynamicQuery(
        @Param("itemType") String itemType,
        @Param("category") String category,
        @Param("rarity") String rarity,
        @Param("keyword") String keyword,
        @Param("available") Boolean available,
        @Param("sortField") String sortField,
        @Param("sortDirection") String sortDirection,
        @Param("coinType") String coinType);
}