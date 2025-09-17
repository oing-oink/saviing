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

        // Repository에서 DB 레벨 처리
        List<Item> items = itemRepository.findItemsWithConditions(
            query.itemType(),
            query.category(),
            query.rarity(),
            query.nameKeyword(),
            query.isAvailable(),
            query.sortField() != null ? query.sortField().name() : null,
            query.sortDirection() != null ? query.sortDirection().name() : null,
            query.coinType() != null ? query.coinType().name() : null
        );

        List<ItemResult> itemResults = items.stream()
            .map(itemResultMapper::toResult)
            .toList();

        ItemListResult result = ItemListResult.of(itemResults);

        log.debug("아이템 목록 조회 완료: 총 {}개", result.totalCount());
        return result;
    }

}