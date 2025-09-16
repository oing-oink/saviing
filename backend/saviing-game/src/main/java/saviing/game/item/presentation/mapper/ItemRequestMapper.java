package saviing.game.item.presentation.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import saviing.game.item.application.dto.query.GetItemQuery;
import saviing.game.item.application.dto.enums.CoinType;
import saviing.game.item.application.dto.enums.SortDirection;
import saviing.game.item.application.dto.enums.SortField;
import saviing.game.item.application.dto.query.GetListItemsQuery;
import saviing.game.item.domain.model.enums.Accessory;
import saviing.game.item.domain.model.enums.Decoration;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Pet;
import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.item.domain.model.enums.category.Category;

/**
 * Presentation layer Request를 Application layer Query로 변환하는 Mapper
 * 요청 파라미터를 적절한 Query 객체로 변환합니다.
 */
@Slf4j
@Component
public class ItemRequestMapper {

    /**
     * 아이템 ID를 GetItemQuery로 변환합니다.
     *
     * @param itemId 아이템 ID
     * @return GetItemQuery
     */
    public GetItemQuery toQuery(Long itemId) {
        return GetItemQuery.builder()
            .itemId(itemId)
            .build();
    }

    /**
     * 요청 파라미터를 ListItemsQuery로 변환합니다.
     *
     * @param type 아이템 타입
     * @param category 아이템 카테고리
     * @param rarity 희귀도
     * @param keyword 검색 키워드
     * @param available 가용성
     * @param sort 정렬 필드
     * @param order 정렬 방향
     * @param coinType 코인 타입
     * @return ListItemsQuery
     */
    public GetListItemsQuery toQuery(
        String type, String category, String rarity,
        String keyword, Boolean available, String sort, String order, String coinType
    ) {
        return GetListItemsQuery.builder()
            .itemType(parseItemType(type))
            .category(parseCategory(category))
            .rarity(parseRarity(rarity))
            .nameKeyword(keyword)
            .isAvailable(available)
            .sortField(parseSortField(sort))
            .sortDirection(parseSortDirection(order))
            .coinType(parseCoinType(coinType))
            .build();
    }

    /**
     * 문자열을 ItemType으로 변환합니다.
     */
    private ItemType parseItemType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return ItemType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 아이템 타입: {}", type);
            return null;
        }
    }

    /**
     * 문자열을 Category로 변환합니다.
     */
    private Category parseCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }

        try {
            String upperCategory = category.toUpperCase();

            // PET 카테고리 시도
            try {
                return Pet.valueOf(upperCategory);
            } catch (IllegalArgumentException ignored) {}

            // ACCESSORY 카테고리 시도
            try {
                return Accessory.valueOf(upperCategory);
            } catch (IllegalArgumentException ignored) {}

            // DECORATION 카테고리 시도
            try {
                return Decoration.valueOf(upperCategory);
            } catch (IllegalArgumentException ignored) {}

            log.warn("유효하지 않은 카테고리: {}", category);
            return null;
        } catch (Exception e) {
            log.warn("카테고리 파싱 오류: {}", category, e);
            return null;
        }
    }

    /**
     * 문자열을 Rarity로 변환합니다.
     */
    private Rarity parseRarity(String rarity) {
        if (rarity == null || rarity.isBlank()) {
            return null;
        }
        try {
            return Rarity.valueOf(rarity.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 희귀도: {}", rarity);
            return null;
        }
    }

    /**
     * 문자열을 SortField로 변환합니다.
     */
    private SortField parseSortField(String sortField) {
        if (sortField == null || sortField.isBlank()) {
            return SortField.NAME; // 기본값
        }
        try {
            return switch (sortField.toLowerCase()) {
                case "name" -> SortField.NAME;
                case "price" -> SortField.PRICE;
                case "rarity" -> SortField.RARITY;
                case "createdat" -> SortField.CREATED_AT;
                case "updatedat" -> SortField.UPDATED_AT;
                default -> {
                    log.warn("유효하지 않은 정렬 필드: {}", sortField);
                    yield SortField.NAME;
                }
            };
        } catch (Exception e) {
            log.warn("정렬 필드 파싱 오류: {}", sortField, e);
            return SortField.NAME;
        }
    }

    /**
     * 문자열을 SortDirection으로 변환합니다.
     */
    private SortDirection parseSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.isBlank()) {
            return SortDirection.ASC; // 기본값
        }
        try {
            return switch (sortDirection.toLowerCase()) {
                case "asc" -> SortDirection.ASC;
                case "desc" -> SortDirection.DESC;
                default -> {
                    log.warn("유효하지 않은 정렬 방향: {}", sortDirection);
                    yield SortDirection.ASC;
                }
            };
        } catch (Exception e) {
            log.warn("정렬 방향 파싱 오류: {}", sortDirection, e);
            return SortDirection.ASC;
        }
    }

    /**
     * 문자열을 CoinType으로 변환합니다.
     */
    private CoinType parseCoinType(String coinType) {
        if (coinType == null || coinType.isBlank()) {
            return CoinType.COIN; // 기본값
        }
        try {
            return switch (coinType.toLowerCase()) {
                case "coin" -> CoinType.COIN;
                case "fishcoin" -> CoinType.FISH_COIN;
                default -> {
                    log.warn("유효하지 않은 코인 타입: {}", coinType);
                    yield CoinType.COIN;
                }
            };
        } catch (Exception e) {
            log.warn("코인 타입 파싱 오류: {}", coinType, e);
            return CoinType.COIN;
        }
    }
}