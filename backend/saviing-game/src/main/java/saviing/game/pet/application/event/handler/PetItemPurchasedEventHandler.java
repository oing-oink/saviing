package saviing.game.pet.application.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import saviing.game.inventory.domain.event.ItemPurchasedEvent;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.repository.ItemRepository;
import saviing.game.pet.application.dto.command.CreatePetCommand;
import saviing.game.pet.application.service.PetCommandService;

/**
 * PET 아이템 구매 이벤트 핸들러
 * ItemPurchasedEvent를 listen하여 PET 타입 아이템 구매 시 pet 테이블에 펫 데이터를 생성합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PetItemPurchasedEventHandler {

    private final ItemRepository itemRepository;
    private final PetCommandService petCommandService;

    /**
     * 아이템 구매 이벤트를 처리합니다.
     * PET 타입 아이템인 경우에만 pet 테이블에 펫을 생성합니다.
     *
     * @param event 아이템 구매 이벤트
     */
    @EventListener
    public void handleItemPurchasedEvent(ItemPurchasedEvent event) {
        log.debug("Processing ItemPurchasedEvent for PET check: characterId={}, itemId={}, itemName={}",
            event.getCharacterId().value(), event.getItemId().value(), event.getItemName());

        try {
            // 1. 구매된 아이템 정보 조회
            Item item = itemRepository.findById(event.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다: " + event.getItemId().value()));

            // 2. PET 타입 아이템인지 확인
            if (item.getItemType() != ItemType.PET) {
                log.debug("Not a PET item, skipping pet creation: itemType={}", item.getItemType());
                return;
            }

            log.info("PET item purchased, creating pet: characterId={}, itemId={}, itemName={}",
                event.getCharacterId().value(), event.getItemId().value(), event.getItemName());

            // 3. 실제 인벤토리 아이템 ID 사용
            InventoryItemId inventoryItemId = event.getInventoryItemId();

            // 4. PetCommandService를 통해 펫 생성
            CreatePetCommand command = CreatePetCommand.of(inventoryItemId);
            petCommandService.createPet(command);

            log.info("Successfully created pet: characterId={}, itemId={}, inventoryItemId={}",
                event.getCharacterId().value(), event.getItemId().value(), inventoryItemId.value());

        } catch (IllegalStateException e) {
            // 이미 존재하는 펫인 경우 (중복 생성 방지)
            log.warn("Pet already exists: characterId={}, itemId={}, error={}",
                event.getCharacterId().value(), event.getItemId().value(), e.getMessage());

        } catch (Exception e) {
            log.error("Failed to process PET ItemPurchasedEvent: characterId={}, itemId={}, itemName={}, error={}",
                event.getCharacterId().value(), event.getItemId().value(), event.getItemName(), e.getMessage(), e);

            // PET 생성 실패는 전체 구매 프로세스를 실패시키지 않도록 예외를 던지지 않음
            // 대신 로그만 남기고 계속 진행
        }
    }
}