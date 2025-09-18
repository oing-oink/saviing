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
import saviing.game.item.application.dto.enums.SortField;
import saviing.game.item.application.dto.enums.SortDirection;
import saviing.game.item.application.dto.enums.CoinType;
import saviing.game.item.domain.exception.ItemNotFoundException;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.domain.model.vo.ItemId;
import saviing.game.item.domain.repository.ItemRepository;

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

        // 쿼리 유효성 검증
        validateQuery(query);

        // 기본값 적용
        String sortField = query.sortField() != null ? query.sortField().name() : SortField.NAME.name();
        String sortDirection = query.sortDirection() != null ? query.sortDirection().name() : SortDirection.ASC.name();
        String coinType = query.coinType() != null ? query.coinType().name() : CoinType.COIN.name();

        // Repository에서 DB 레벨 처리
        List<Item> items = itemRepository.findItemsWithConditions(
            query.itemType(),
            query.category(),
            query.rarity(),
            query.nameKeyword(),
            query.isAvailable(),
            sortField,
            sortDirection,
            coinType
        );

        List<ItemResult> itemResults = items.stream()
            .map(itemResultMapper::toResult)
            .toList();

        ItemListResult result = ItemListResult.of(itemResults);

        log.debug("아이템 목록 조회 완료: 총 {}개", result.totalCount());
        return result;
    }

    /**
     * 쿼리 파라미터의 유효성을 검증합니다.
     *
     * @param query 검증할 쿼리
     * @throws IllegalArgumentException 유효하지 않은 파라미터가 있는 경우
     */
    private void validateQuery(GetListItemsQuery query) {
        // 카테고리와 아이템 타입 일치성 검증
        if (query.category() != null && query.itemType() != null) {
            if (!query.itemType().isValidCategory(query.category())) {
                throw new IllegalArgumentException(
                    String.format("카테고리 %s는 아이템 타입 %s에 속하지 않습니다",
                        query.category().name(), query.itemType().name())
                );
            }
        }

        // 가격 정렬 시 코인 타입 필수 검증
        if (SortField.PRICE.equals(query.sortField()) && query.coinType() == null) {
            log.warn("가격 정렬 시 코인 타입이 지정되지 않아 기본값(COIN) 사용");
        }

        // 검색 키워드 길이 검증
        if (query.nameKeyword() != null && query.nameKeyword().trim().length() > 100) {
            throw new IllegalArgumentException("검색 키워드는 100자를 초과할 수 없습니다");
        }
    }

}