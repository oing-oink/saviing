package saviing.game.shop.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.shop.application.dto.query.GetGachaInfoQuery;
import saviing.game.shop.application.dto.result.GachaInfoResult;
import saviing.game.shop.domain.model.gacha.GachaPool;
import saviing.game.item.domain.repository.ItemRepository;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.shop.application.mapper.GachaItemInfoMapper;

import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 상점 조회 관련 애플리케이션 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopQueryService {

    private final ItemRepository itemRepository;
    private final GachaItemInfoMapper gachaItemInfoMapper;
    private static final GachaPool GACHA_POOL = GachaPool.DEFAULT;

    /**
     * 가챠 정보를 조회합니다.
     *
     * @param query 가챠 정보 조회 쿼리
     * @return 가챠 정보
     */
    @Transactional(readOnly = true)
    public GachaInfoResult getGachaInfo(GetGachaInfoQuery query) {
        log.info("가챠 정보 조회 요청: onlyActive={}", query.isOnlyActive());

        GachaInfoResult result = GachaInfoResult.builder()
            .gachaPoolId(GACHA_POOL.id())
            .gachaPoolName(GACHA_POOL.poolName())
            .gachaInfo(GachaInfoResult.GachaInfo.builder()
                .drawPrice(GachaInfoResult.PriceInfo.builder()
                    .coin(GACHA_POOL.price().coin())
                    .fishCoin(GACHA_POOL.price().fishCoin())
                    .build())
                .dropRates(GACHA_POOL.dropRates())
                .rewardItemIds(getRewardItemsByRarity())
                .build())
            .build();

        log.info("가챠 정보 조회 완료: 가챠풀 {} ({})", result.gachaPoolId(), result.gachaPoolName());
        return result;
    }

    /**
     * 희귀도별 보상 아이템 ID와 이름을 조회합니다.
     * 모든 사용 가능한 아이템을 한번에 조회하여 희귀도별로 분류합니다.
     *
     * @return 희귀도별 아이템 ID 목록
     */
    private Map<Rarity, List<GachaInfoResult.ItemInfo>> getRewardItemsByRarity() {
        // 모든 사용 가능한 아이템을 한번에 조회
        List<Item> allAvailableItems = itemRepository.findItemsWithConditions(
            null,      // itemType: 모든 타입
            null,      // category: 모든 카테고리
            null,      // rarity: 모든 희귀도
            null,      // keyword: 검색어 없음
            true,      // available: 사용 가능한 것만
            "rarity",  // sortField: 희귀도로 정렬
            "asc",     // sortDirection: 오름차순
            null       // coinType: 모든 코인 타입
        );

        // 희귀도별로 그룹핑 (매퍼 사용)
        Map<Rarity, List<GachaInfoResult.ItemInfo>> rewardItems = allAvailableItems.stream()
            .collect(Collectors.groupingBy(
                Item::getRarity,
                () -> new EnumMap<>(Rarity.class),
                Collectors.mapping(
                    gachaItemInfoMapper::toItemInfo,
                    Collectors.toList()
                )
            ));

        // 빈 희귀도에 대해서도 빈 리스트 추가
        for (Rarity rarity : Rarity.values()) {
            rewardItems.putIfAbsent(rarity, new ArrayList<>());
        }

        return rewardItems;
    }
}
