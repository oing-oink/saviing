package saviing.game.inventory.application.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import saviing.game.inventory.application.dto.command.AddInventoryItemCommand;
import saviing.game.inventory.application.service.InventoryCommandService;
import saviing.game.inventory.domain.event.ItemPurchasedEvent;

/**
 * 사용안됨 - 직접 서비스 호출로 대체
 *
 * 아이템 구매 이벤트 핸들러
 * Shop 도메인에서 발행하는 ItemPurchasedEvent를 동기적으로 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ItemPurchasedEventHandler {

    private final InventoryCommandService inventoryCommandService;

    /**
     * 아이템 구매 이벤트를 처리합니다.
     * Shop 도메인에서 아이템 구매 완료 시 inventory에 아이템을 추가합니다.
     *
     * @param event 아이템 구매 이벤트
     */
    @EventListener
    public void handleItemPurchasedEvent(ItemPurchasedEvent event) {
        log.info("Processing ItemPurchasedEvent: characterId={}, itemId={}, itemName={}",
            event.getCharacterId().value(), event.getItemId().value(), event.getItemName());

        try {
            AddInventoryItemCommand command = AddInventoryItemCommand.of(
                event.getCharacterId(),
                event.getItemId()
            );

            inventoryCommandService.addInventoryItem(command);

            log.info("Successfully processed ItemPurchasedEvent: characterId={}, itemId={}, itemName={}",
                event.getCharacterId().value(), event.getItemId().value(), event.getItemName());

        } catch (Exception e) {
            log.error("Failed to process ItemPurchasedEvent: characterId={}, itemId={}, itemName={}, error={}",
                event.getCharacterId().value(), event.getItemId().value(), event.getItemName(), e.getMessage(), e);
            throw new RuntimeException("아이템 구매 이벤트 처리 실패: " + e.getMessage(), e);
        }
    }
}