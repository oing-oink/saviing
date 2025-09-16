package saviing.game.item.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.item.application.dto.query.GetItemQuery;
import saviing.game.item.application.dto.query.GetListItemsQuery;
import saviing.game.item.application.dto.result.ItemListResult;
import saviing.game.item.application.dto.result.ItemResult;
import saviing.game.item.application.mapper.ItemResultMapper;
import saviing.game.item.domain.exception.ItemNotFoundException;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.application.dto.enums.CoinType;
import saviing.game.item.application.dto.enums.SortDirection;
import saviing.game.item.application.dto.enums.SortField;
import saviing.game.item.domain.model.vo.ItemId;
import saviing.game.item.domain.repository.ItemRepository;

import java.util.Comparator;
import java.util.List;

/**
 * 아이템 조회 서비스
 * 아이템의 조회 작업을 처리합니다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemQueryService {

    private final ItemRepository itemRepository;
    private final ItemResultMapper itemResultMapper;

    /**
     * 단일 아이템을 조회합니다.
     *
     * @param query 아이템 조회 쿼리
     * @return 조회된 아이템 결과
     */
    public ItemResult getItem(GetItemQuery query) {
        log.debug("아이템 조회 시작: ID={}", query.itemId());

        ItemId itemId = ItemId.of(query.itemId());
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> ItemNotFoundException.withItemId(itemId));

        ItemResult result = itemResultMapper.toResult(item);

        log.debug("아이템 조회 완료: ID={}", query.itemId());
        return result;
    }

    /**
     * 아이템 목록을 조회합니다.
     *
     * @param query 목록 조회 쿼리 (검색 조건 및 정렬 포함)
     * @return 조회된 아이템 목록 결과
     */
    public ItemListResult listItems(GetListItemsQuery query) {
        log.debug("아이템 목록 조회 시작: {}", query);

        List<Item> items = findItemsByQuery(query);

        // 정렬 적용
        items = applySorting(items, query);

        List<ItemResult> itemResults = items.stream()
            .map(itemResultMapper::toResult)
            .toList();

        ItemListResult result = ItemListResult.of(itemResults);

        log.debug("아이템 목록 조회 완료: 총 {}개", result.totalCount());
        return result;
    }


    /**
     * 쿼리 조건에 따라 아이템을 조회합니다.
     *
     * @param query 조회 쿼리
     * @return 조회된 아이템 목록
     */
    private List<Item> findItemsByQuery(GetListItemsQuery query) {
        List<Item> items;

        // 1. 기본 조건으로 아이템 조회
        boolean availableOnly = query.isAvailable() == null || query.isAvailable();

        if (availableOnly) {
            if (query.category() != null) {
                items = itemRepository.findAvailableByCategory(query.category());
            } else if (query.itemType() != null) {
                items = itemRepository.findAvailableByType(query.itemType());
            } else {
                items = itemRepository.findAllAvailable();
            }
        } else {
            if (query.category() != null) {
                items = itemRepository.findByCategory(query.category());
            } else if (query.itemType() != null) {
                items = itemRepository.findByType(query.itemType());
            } else if (query.rarity() != null) {
                items = itemRepository.findByRarity(query.rarity());
            } else {
                items = itemRepository.findAll();
            }
        }

        // 2. 추가 필터링 적용 (메모리에서 처리)
        return items.stream()
            .filter(item -> matchesQuery(item, query))
            .toList();
    }

    /**
     * 아이템이 쿼리 조건에 일치하는지 확인합니다.
     *
     * @param item 확인할 아이템
     * @param query 쿼리 조건
     * @return 조건 일치 여부
     */
    private boolean matchesQuery(Item item, GetListItemsQuery query) {
        // 키워드 검색
        if (query.nameKeyword() != null && !query.nameKeyword().isBlank()) {
            if (!item.getItemName().value().toLowerCase()
                    .contains(query.nameKeyword().toLowerCase())) {
                return false;
            }
        }

        // 희귀도 필터링
        if (query.rarity() != null && !query.rarity().equals(item.getRarity())) {
            return false;
        }

        // 아이템 타입 필터링 (카테고리가 지정되지 않은 경우에만)
        if (query.category() == null && query.itemType() != null
            && !query.itemType().equals(item.getItemType())) {
            return false;
        }

        return true;
    }

    /**
     * 아이템 목록에 정렬을 적용합니다.
     *
     * @param items 정렬할 아이템 목록
     * @param query 정렬 조건이 포함된 쿼리
     * @return 정렬된 아이템 목록
     */
    private List<Item> applySorting(List<Item> items, GetListItemsQuery query) {
        if (query.sortField() == null) {
            return items;
        }

        Comparator<Item> comparator = createComparator(query.sortField(), query.coinType());

        if (SortDirection.DESC.equals(query.sortDirection())) {
            comparator = comparator.reversed();
        }

        return items.stream()
            .sorted(comparator)
            .toList();
    }

    /**
     * 정렬 필드에 따른 Comparator를 생성합니다.
     *
     * @param sortField 정렬 필드
     * @param coinType 코인 타입 (가격 정렬시만 사용)
     * @return Comparator
     */
    private Comparator<Item> createComparator(SortField sortField, CoinType coinType) {
        return switch (sortField) {
            case NAME -> Comparator.comparing(item -> item.getItemName().value());
            case PRICE -> createPriceComparator(coinType);
            case RARITY -> Comparator.comparing(item -> item.getRarity().ordinal());
            case CREATED_AT -> Comparator.comparing(Item::getCreatedAt);
            case UPDATED_AT -> Comparator.comparing(Item::getUpdatedAt);
        };
    }

    /**
     * 가격 기준 Comparator를 생성합니다.
     *
     * @param coinType 코인 타입
     * @return 가격 기준 Comparator
     */
    private Comparator<Item> createPriceComparator(CoinType coinType) {
        if (CoinType.FISH_COIN.equals(coinType)) {
            return Comparator.comparing(item ->
                item.getPrice().fishCoin() != null ? item.getPrice().fishCoin() : Integer.MAX_VALUE
            );
        } else {
            // 기본값은 일반 코인
            return Comparator.comparing(item ->
                item.getPrice().coin() != null ? item.getPrice().coin() : Integer.MAX_VALUE
            );
        }
    }
}