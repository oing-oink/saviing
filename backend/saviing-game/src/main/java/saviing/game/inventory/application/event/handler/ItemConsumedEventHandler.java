package saviing.game.inventory.application.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import saviing.game.inventory.application.dto.command.ConsumeInventoryItemCommand;
import saviing.game.inventory.application.service.InventoryCommandService;
import saviing.game.inventory.domain.event.ItemConsumedEvent;

/**
 * 아이템 소모 이벤트 핸들러
 * Pet 도메인에서 발행하는 ItemConsumedEvent를 동기적으로 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ItemConsumedEventHandler {

    private final InventoryCommandService inventoryCommandService;

    /**
     * 아이템 소모 이벤트를 처리합니다.
     * Pet 도메인에서 소모품 사용 시 inventory의 개수를 감소시킵니다.
     *
     * @param event 아이템 소모 이벤트
     */
    @EventListener
    public void handleItemConsumedEvent(ItemConsumedEvent event) {
        log.info("Processing ItemConsumedEvent: inventoryItemId={}, characterId={}, consumedQuantity={}",
            event.getInventoryItemId().value(), event.getCharacterId().value(), event.getConsumedQuantity());

        try {
            ConsumeInventoryItemCommand command = ConsumeInventoryItemCommand.consume(
                event.getInventoryItemId(),
                event.getCharacterId(),
                event.getConsumedQuantity()
            );

            inventoryCommandService.consumeInventoryItem(command);

            log.info("Successfully processed ItemConsumedEvent: inventoryItemId={}",
                event.getInventoryItemId().value());

        } catch (Exception e) {
            log.error("Failed to process ItemConsumedEvent: inventoryItemId={}, error={}",
                event.getInventoryItemId().value(), e.getMessage(), e);
            throw new RuntimeException("아이템 소모 이벤트 처리 실패: " + e.getMessage(), e);
        }
    }
}