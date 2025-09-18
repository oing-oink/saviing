package saviing.game.item.application.dto.query;

import lombok.Builder;
import saviing.game.item.application.dto.enums.CoinType;
import saviing.game.item.application.dto.enums.SortDirection;
import saviing.game.item.application.dto.enums.SortField;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.item.domain.model.enums.category.Category;

/**
 * 아이템 목록 조회 쿼리 DTO
 * 검색 조건과 정렬 옵션을 포함합니다.
 */
@Builder
public record GetListItemsQuery(
    // 검색 조건
    ItemType itemType,
    Category category,
    Rarity rarity,
    String nameKeyword,
    Boolean isAvailable,

    // 정렬 옵션
    SortField sortField,
    SortDirection sortDirection,
    CoinType coinType
) {

    /**
     * 기본 쿼리를 생성합니다 (판매 가능한 아이템만, 이름 오름차순).
     *
     * @return 기본 ListItemsQuery
     */
    public static GetListItemsQuery defaultQuery() {
        return GetListItemsQuery.builder()
            .isAvailable(true)
            .sortField(SortField.NAME)
            .sortDirection(SortDirection.ASC)
            .build();
    }

    /**
     * 특정 카테고리의 아이템을 조회하는 쿼리를 생성합니다.
     *
     * @param category 조회할 카테고리
     * @return 카테고리별 ListItemsQuery
     */
    public static GetListItemsQuery byCategory(Category category) {
        return GetListItemsQuery.builder()
            .category(category)
            .isAvailable(true)
            .sortField(SortField.NAME)
            .sortDirection(SortDirection.ASC)
            .build();
    }

    /**
     * 특정 희귀도의 아이템을 조회하는 쿼리를 생성합니다.
     *
     * @param rarity 조회할 희귀도
     * @return 희귀도별 ListItemsQuery
     */
    public static GetListItemsQuery byRarity(Rarity rarity) {
        return GetListItemsQuery.builder()
            .rarity(rarity)
            .isAvailable(true)
            .sortField(SortField.NAME)
            .sortDirection(SortDirection.ASC)
            .build();
    }

    /**
     * 가격 정렬 쿼리인지 확인합니다.
     *
     * @return 가격 정렬 여부
     */
    public boolean isPriceSort() {
        return SortField.PRICE.equals(sortField);
    }

    /**
     * 검색 조건이 있는지 확인합니다.
     *
     * @return 검색 조건 존재 여부
     */
    public boolean hasSearchCondition() {
        return itemType != null || category != null || rarity != null ||
               nameKeyword != null;
    }
}