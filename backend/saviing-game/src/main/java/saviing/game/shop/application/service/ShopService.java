package saviing.game.shop.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.character.application.dto.command.DebitCoinsCommand;
import saviing.game.character.application.service.CharacterCommandService;
import saviing.game.character.application.service.CharacterQueryService;
import saviing.game.character.domain.model.vo.CharacterId;  // TODO: domain 참조 개선
import saviing.game.item.application.dto.query.GetItemQuery;
import saviing.game.item.application.dto.result.ItemResult;
import saviing.game.item.application.service.ItemQueryService;
import saviing.game.shop.application.dto.command.PurchaseItemCommand;
import saviing.game.character.application.dto.query.GetCharacterQuery;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.shop.application.dto.result.PurchaseResult;
import saviing.game.shop.domain.exception.PurchaseException;
import saviing.game.shop.domain.exception.ShopException;
import saviing.game.shop.domain.model.aggregate.PurchaseRecord;
import saviing.game.shop.domain.model.vo.PaymentMethod;
import saviing.game.shop.domain.repository.PurchaseRecordRepository;

/**
 * 상점 애플리케이션 서비스입니다.
 * 아이템 구매 프로세스를 동기식으로 처리하며, 3개 도메인과의 통신을 관리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {

    private final PurchaseRecordRepository purchaseRecordRepository;
    private final ItemQueryService itemQueryService;
    private final CharacterQueryService characterQueryService;
    private final CharacterCommandService characterCommandService;
    private final InventoryMockService inventoryMockService;

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
                .paymentMethod(command.paymentMethod())
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
        String paidCurrency = "COIN".equals(command.paymentMethod()) ? "COIN" : "FISH_COIN";

        PurchaseRecord purchaseRecord = PurchaseRecord.builder()
            .characterId(command.characterId())
            .itemId(command.itemId())
            .paymentMethod(PaymentMethod.valueOf(command.paymentMethod()))
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
            ItemResult item = itemQueryService.getItem(GetItemQuery.builder().itemId(command.itemId()).build());

            if (!item.isAvailable()) {
                throw PurchaseException.itemUnavailable(command.itemId());
            }

            if ("COIN".equals(command.paymentMethod()) && (item.coin() == null || item.coin() <= 0)) {
                throw PurchaseException.paymentMethodNotSupported(command.itemId(), "COIN");
            }

            if ("FISH_COIN".equals(command.paymentMethod()) && (item.fishCoin() == null || item.fishCoin() <= 0)) {
                throw PurchaseException.paymentMethodNotSupported(command.itemId(), "FISH_COIN");
            }

            log.info("아이템 검증 완료: itemId={}, itemName={}", item.itemId(), item.itemName());
            return item;

        } catch (PurchaseException e) {
            throw e;
        } catch (Exception e) {
            throw PurchaseException.itemValidationFailed(command.itemId(), e);
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
            Integer coinAmount = null;
            Integer fishCoinAmount = null;

            if ("COIN".equals(command.paymentMethod())) {
                coinAmount = item.coin();
            } else if ("FISH_COIN".equals(command.paymentMethod())) {
                fishCoinAmount = item.fishCoin();
            }

            // 잔액 확인
            if (!characterQueryService.hasSufficientFunds(command.characterId(), coinAmount, fishCoinAmount)) {
                throw PurchaseException.insufficientFunds(command.characterId(), command.paymentMethod());
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
     * 인벤토리 도메인과 통신하여 아이템을 지급합니다.
     *
     * @param command 구매 명령
     * @param item 아이템 정보
     * @throws ShopException 아이템 지급 실패 시
     */
    private void processItemGrant(PurchaseItemCommand command, ItemResult item) {
        log.info("아이템 지급 시작: characterId={}, itemId={}", command.characterId(), command.itemId());

        try {
            inventoryMockService.grantItemToCharacter(command.characterId(), item.itemId(), item.itemName());
            log.info("아이템 지급 완료: characterId={}, itemId={}", command.characterId(), command.itemId());

        } catch (Exception e) {
            log.error("아이템 지급 실패: characterId={}, itemId={}, error={}",
                command.characterId(), command.itemId(), e.getMessage());
            throw PurchaseException.itemGrantFailed(command.characterId(), command.itemId(), e);
        }
    }
}