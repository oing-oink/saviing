package saviing.game.shop.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.character.application.dto.command.DebitCoinsCommand;
import saviing.game.character.application.service.CharacterCommandService;
import saviing.game.character.application.service.CharacterQueryService;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.application.dto.command.AddInventoryItemCommand;
import saviing.game.inventory.application.service.InventoryCommandService;
import saviing.game.item.application.dto.query.GetItemQuery;
import saviing.game.item.application.dto.result.ItemResult;
import saviing.game.item.application.service.ItemQueryService;
import saviing.game.item.domain.repository.ItemRepository;
import saviing.game.item.domain.model.vo.ItemId;
import saviing.game.item.domain.model.vo.Price;
import saviing.game.shop.application.dto.command.PurchaseItemCommand;
import saviing.game.character.application.dto.query.GetCharacterQuery;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.shop.application.dto.result.PurchaseResult;
import saviing.game.shop.domain.exception.PurchaseException;
import saviing.game.shop.domain.exception.ShopException;
import saviing.game.shop.domain.exception.ShopErrorCode;
import saviing.game.shop.domain.model.aggregate.PurchaseRecord;
import saviing.game.shop.domain.model.vo.PaymentMethod;
import saviing.game.shop.domain.repository.PurchaseRecordRepository;
import saviing.game.shop.application.dto.command.DrawGachaCommand;
import saviing.game.shop.application.dto.result.GachaDrawResult;
import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.shop.domain.model.gacha.GachaPool;
import saviing.game.item.domain.model.aggregate.Item;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 상점 애플리케이션 서비스입니다.
 * 아이템 구매 프로세스를 동기식으로 처리하며, 3개 도메인과의 통신을 관리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopCommandService {

    private final PurchaseRecordRepository purchaseRecordRepository;
    private final ItemQueryService itemQueryService;
    private final CharacterQueryService characterQueryService;
    private final CharacterCommandService characterCommandService;
    private final InventoryCommandService inventoryCommandService;
    private final ItemRepository itemRepository;

    private static final GachaPool GACHA_POOL = GachaPool.DEFAULT;

    /**
     * 아이템 구매를 동기식으로 처리합니다.
     * 모든 검증과 처리를 메모리에서 수행하고, 성공 시에만 최종 구매 기록을 저장합니다.
     *
     * @param command 구매 명령
     * @return 구매 결과
     * @throws ShopException 구매 조건이 충족되지 않거나 처리 중 오류 발생 시
     */
    @Transactional
    public PurchaseResult requestItemPurchase(PurchaseItemCommand command) {
        log.info("아이템 구매 요청: characterId={}, itemId={}, paymentMethod={}",
            command.characterId(), command.itemId(), command.paymentMethod());

        try {
            // 1. 명령 유효성 검증
            command.validate();

            // 2. 아이템 도메인 통신: 아이템 검증
            ItemResult item = processItemValidation(command);

            // 3. 캐릭터 도메인 통신: 잔액 확인 및 차감
            Integer paidAmount = processFundsDebit(command, item);

            // 4. 인벤토리 도메인 통신: 아이템 지급
            processItemGrant(command, item);

            // 5. 성공 시에만 구매 기록 저장
            PurchaseRecord purchaseRecord = savePurchaseRecord(command, item, paidAmount);

            log.info("구매 완료: characterId={}, itemId={}, paidAmount={}",
                command.characterId(), command.itemId(), paidAmount);

            // 구매 완료 후 아이템 정보와 캐릭터 잔액 조회
            CharacterResult characterResult = characterQueryService.getCharacter(
                GetCharacterQuery.builder().characterId(CharacterId.of(command.characterId())).build()
            );

            return PurchaseResult.builder()
                .item(item)
                .character(characterResult)
                .paymentMethod(command.paymentMethod().getCurrency())
                .build();

        } catch (PurchaseException e) {
            log.error("구매 실패: characterId={}, itemId={}, error={}",
                command.characterId(), command.itemId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("예상치 못한 구매 실패: characterId={}, itemId={}, error={}",
                command.characterId(), command.itemId(), e.getMessage());
            throw PurchaseException.processingFailed(e.getMessage(), e);
        }
    }





    /**
     * 성공한 구매 기록을 저장합니다.
     *
     * @param command 구매 명령
     * @param item 아이템 정보
     * @param paidAmount 결제 금액
     * @return 저장된 구매 기록
     */
    private PurchaseRecord savePurchaseRecord(PurchaseItemCommand command, ItemResult item, Integer paidAmount) {
        PaymentMethod paymentMethod = command.paymentMethod();
        String paidCurrency = paymentMethod.getCurrency();

        PurchaseRecord purchaseRecord = PurchaseRecord.builder()
            .characterId(command.characterId())
            .itemId(command.itemId())
            .paymentMethod(paymentMethod)
            .paidAmount(paidAmount)
            .paidCurrency(paidCurrency)
            .build();

        return purchaseRecordRepository.save(purchaseRecord);
    }


    /**
     * 아이템 도메인과 통신하여 아이템을 검증합니다.
     *
     * @param command 구매 명령
     * @return 검증된 아이템 정보
     * @throws ShopException 아이템 검증 실패 시
     */
    private ItemResult processItemValidation(PurchaseItemCommand command) {
        log.info("아이템 검증 시작: itemId={}", command.itemId());

        try {
            PaymentMethod paymentMethod = command.paymentMethod();
            ItemResult item = itemQueryService.getItem(GetItemQuery.builder().itemId(command.itemId()).build());

            if (!item.isAvailable()) {
                throw PurchaseException.itemUnavailable(command.itemId());
            }

            if (paymentMethod.isCoin() && (item.coin() == null || item.coin() <= 0)) {
                throw PurchaseException.paymentMethodNotSupported(command.itemId(), paymentMethod);
            }

            if (paymentMethod.isFishCoin() && (item.fishCoin() == null || item.fishCoin() <= 0)) {
                throw PurchaseException.paymentMethodNotSupported(command.itemId(), paymentMethod);
            }

            log.info("아이템 검증 완료: itemId={}, itemName={}", item.itemId(), item.itemName());
            return item;

        } catch (PurchaseException e) {
            throw e;
        } catch (Exception e) {
            throw new ShopException(ShopErrorCode.PURCHASE_ITEM_VALIDATION_FAILED, "아이템 검증 중 오류가 발생했습니다", e);
        }
    }

    /**
     * 캐릭터 도메인과 통신하여 자금을 차감합니다.
     *
     * @param command 구매 명령
     * @param item 아이템 정보
     * @return 차감된 금액
     * @throws ShopException 잔액 부족 또는 자금 차감 실패 시
     */
    private Integer processFundsDebit(PurchaseItemCommand command, ItemResult item) {
        log.info("자금 차감 시작: characterId={}, paymentMethod={}", command.characterId(), command.paymentMethod());

        try {
            PaymentMethod paymentMethod = command.paymentMethod();
            Integer coinAmount = null;
            Integer fishCoinAmount = null;

            if (paymentMethod.isCoin()) {
                coinAmount = item.coin();
            } else if (paymentMethod.isFishCoin()) {
                fishCoinAmount = item.fishCoin();
            }

            // 잔액 확인
            if (!characterQueryService.hasSufficientFunds(command.characterId(), coinAmount, fishCoinAmount)) {
                throw PurchaseException.insufficientFunds(command.characterId(), paymentMethod);
            }

            // 자금 차감
            DebitCoinsCommand debitCommand;
            Integer paidAmount;
            if (coinAmount != null) {
                debitCommand = DebitCoinsCommand.coin(CharacterId.of(command.characterId()), coinAmount);
                paidAmount = coinAmount;
            } else {
                debitCommand = DebitCoinsCommand.fishCoin(CharacterId.of(command.characterId()), fishCoinAmount);
                paidAmount = fishCoinAmount;
            }

            characterCommandService.debitCoins(debitCommand);
            log.info("자금 차감 완료: characterId={}, amount={}", command.characterId(), paidAmount);

            return paidAmount;

        } catch (PurchaseException e) {
            throw e;
        } catch (Exception e) {
            throw PurchaseException.fundsDebitFailed(command.characterId(), e);
        }
    }

    /**
     * 인벤토리 도메인과 직접 통신하여 아이템을 지급합니다.
     * InventoryCommandService를 직접 호출하여 동기적으로 처리합니다.
     *
     * @param command 구매 명령
     * @param item 아이템 정보
     * @throws ShopException 아이템 지급 실패 시
     */
    private void processItemGrant(PurchaseItemCommand command, ItemResult item) {
        log.info("아이템 지급 시작: characterId={}, itemId={}", command.characterId(), command.itemId());

        try {
            // 아이템 타입에 따라 적절한 Command 생성
            AddInventoryItemCommand addCommand;
            if (item.itemType().name().equals("CONSUMPTION")) {
                // 소모품의 경우 count 포함
                addCommand = AddInventoryItemCommand.withCount(
                    CharacterId.of(command.characterId()),
                    ItemId.of(command.itemId()),
                    1  // 구매 시 기본 1개
                );
            } else {
                // 일반 아이템
                addCommand = AddInventoryItemCommand.of(
                    CharacterId.of(command.characterId()),
                    ItemId.of(command.itemId())
                );
            }

            inventoryCommandService.addInventoryItem(addCommand);

            log.info("아이템 지급 완료: characterId={}, itemId={}", command.characterId(), command.itemId());

        } catch (Exception e) {
            log.error("아이템 지급 실패: characterId={}, itemId={}, error={}",
                command.characterId(), command.itemId(), e.getMessage());
            throw PurchaseException.itemGrantFailed(command.characterId(), command.itemId(), e);
        }
    }

    /**
     * 가챠 뽑기를 실행합니다.
     *
     * @param command 가챠 뽑기 명령
     * @return 가챠 뽑기 결과
     */
    @Transactional
    public GachaDrawResult drawGacha(DrawGachaCommand command) {
        log.info("가챠 뽑기 시작: characterId={}, gachaPoolId={}, paymentMethod={}",
            command.characterId(), command.gachaPoolId(), command.paymentMethod());

        try {
            // 1. 명령 유효성 검증
            command.validate();

            // 2. 가챠풀 검증
            validateGachaPoolId(command.gachaPoolId());

            // 3. 희귀도 결정
            Rarity drawnRarity = GACHA_POOL.drawRarity();
            log.debug("뽑기 희귀도 결정: {}", drawnRarity);

            // 4. 해당 희귀도의 아이템 중 랜덤 선택
            ItemResult selectedItem = selectRandomItem(drawnRarity);

            // 5. 기존 구매 프로세스 재활용하여 아이템 지급
            PurchaseItemCommand purchaseCommand = PurchaseItemCommand.builder()
                .characterId(command.characterId())
                .itemId(selectedItem.itemId())
                .paymentMethod(command.paymentMethod())
                .build();

            // 가챠풀 가격으로 구매 처리 (실제 아이템 가격 무시)
            processGachaPurchase(purchaseCommand, GACHA_POOL.price(), selectedItem);

            // 6. 구매 완료 후 캐릭터 정보 조회
            CharacterResult characterResult = characterQueryService.getCharacter(
                GetCharacterQuery.builder().characterId(CharacterId.of(command.characterId())).build()
            );

            GachaDrawResult result = GachaDrawResult.builder()
                .gachaPoolId(GACHA_POOL.id())
                .gachaPoolName(GACHA_POOL.poolName())
                .drawnItem(selectedItem)
                .character(characterResult)
                .paymentCurrency(command.paymentMethod().getCurrency())
                .build();

            log.info("가챠 뽑기 완료: characterId={}, drawnItem={}, rarity={}",
                command.characterId(), selectedItem.itemName(), drawnRarity);

            return result;

        } catch (PurchaseException e) {
            log.error("가챠 뽑기 실패: characterId={}, gachaPoolId={}, error={}",
                command.characterId(), command.gachaPoolId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("예상치 못한 가챠 뽑기 실패: characterId={}, gachaPoolId={}, error={}",
                command.characterId(), command.gachaPoolId(), e.getMessage());
            throw PurchaseException.processingFailed("가챠 뽑기 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private void validateGachaPoolId(Long gachaPoolId) {
        if (gachaPoolId == null || gachaPoolId.longValue() != GACHA_POOL.id()) {
            throw PurchaseException.processingFailed("존재하지 않는 가챠풀입니다: " + gachaPoolId, null);
        }
    }

    private ItemResult selectRandomItem(Rarity rarity) {
        try {
            // 해당 희귀도의 사용 가능한 아이템들을 모두 조회
            List<Item> availableItems = itemRepository.findItemsWithConditions(
                null,                    // itemType: 모든 타입
                null,                    // category: 모든 카테고리
                rarity,                  // rarity: 특정 희귀도만
                null,                    // keyword: 검색어 없음
                true,                    // available: 사용 가능한 것만
                null,                    // sortField: 정렬 없음
                null,                    // sortDirection: 정렬 없음
                null                     // coinType: 모든 코인 타입
            );

            if (availableItems.isEmpty()) {
                throw PurchaseException.processingFailed("해당 희귀도의 사용 가능한 아이템이 없습니다: " + rarity, null);
            }

            // 랜덤으로 하나 선택
            int randomIndex = ThreadLocalRandom.current().nextInt(availableItems.size());
            Item selectedItem = availableItems.get(randomIndex);

            // ItemResult로 변환
            ItemResult item = itemQueryService.getItem(GetItemQuery.builder().itemId(selectedItem.getItemId().value()).build());
            return item;

        } catch (PurchaseException e) {
            throw e;
        } catch (Exception e) {
            throw PurchaseException.processingFailed("아이템 선택 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private void processGachaPurchase(PurchaseItemCommand purchaseCommand, Price drawPrice, ItemResult selectedItem) {
        try {
            Integer paidAmount = processGachaFundsDebit(purchaseCommand, drawPrice);
            processItemGrant(purchaseCommand, selectedItem);
            savePurchaseRecord(purchaseCommand, selectedItem, paidAmount);
        } catch (PurchaseException e) {
            throw e;
        } catch (Exception e) {
            throw PurchaseException.processingFailed("가챠 구매 처리 실패: " + e.getMessage(), e);
        }
    }

    private Integer processGachaFundsDebit(PurchaseItemCommand command, Price drawPrice) {
        PaymentMethod paymentMethod = command.paymentMethod();
        Integer coinAmount = null;
        Integer fishCoinAmount = null;

        if (paymentMethod.isCoin() && drawPrice.coin() != null && drawPrice.coin() > 0) {
            coinAmount = drawPrice.coin();
        } else if (paymentMethod.isFishCoin() && drawPrice.fishCoin() != null && drawPrice.fishCoin() > 0) {
            fishCoinAmount = drawPrice.fishCoin();
        }

        if (coinAmount == null && fishCoinAmount == null) {
            throw PurchaseException.paymentMethodNotSupported(null, paymentMethod);
        }

        if (!characterQueryService.hasSufficientFunds(command.characterId(), coinAmount, fishCoinAmount)) {
            throw PurchaseException.insufficientFunds(command.characterId(), paymentMethod);
        }

        DebitCoinsCommand debitCommand;
        Integer paidAmount;
        if (coinAmount != null) {
            debitCommand = DebitCoinsCommand.coin(CharacterId.of(command.characterId()), coinAmount);
            paidAmount = coinAmount;
        } else {
            debitCommand = DebitCoinsCommand.fishCoin(CharacterId.of(command.characterId()), fishCoinAmount);
            paidAmount = fishCoinAmount;
        }

        characterCommandService.debitCoins(debitCommand);
        return paidAmount;
    }
}
