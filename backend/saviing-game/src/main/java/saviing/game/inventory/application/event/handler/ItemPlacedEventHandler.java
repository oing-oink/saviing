package saviing.game.inventory.application.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import saviing.game.inventory.application.dto.command.PlaceInventoryItemCommand;
import saviing.game.inventory.application.service.InventoryCommandService;
import saviing.game.inventory.domain.event.ItemPlacedEvent;

/**
 * 아이템 배치 이벤트 핸들러
 * Room 도메인에서 발행하는 ItemPlacedEvent를 동기적으로 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ItemPlacedEventHandler {

    private final InventoryCommandService inventoryCommandService;

    /**
     * 아이템 배치 이벤트를 처리합니다.
     * Room 도메인에서 아이템 배치 시 inventory의 위치 정보를 업데이트합니다.
     *
     * @param event 아이템 배치 이벤트
     */
    @EventListener
    public void handleItemPlacedEvent(ItemPlacedEvent event) {
        log.info("Processing ItemPlacedEvent: inventoryItemId={}, characterId={}, roomId={}",
            event.getInventoryItemId().value(), event.getCharacterId().value(), event.getRoomId());

        try {
            PlaceInventoryItemCommand command = PlaceInventoryItemCommand.of(
                event.getInventoryItemId(),
                event.getCharacterId(),
                event.getRoomId()
            );

            inventoryCommandService.placeInventoryItem(command);

            log.info("Successfully processed ItemPlacedEvent: inventoryItemId={}, roomId={}",
                event.getInventoryItemId().value(), event.getRoomId());

        } catch (Exception e) {
            log.error("Failed to process ItemPlacedEvent: inventoryItemId={}, roomId={}, error={}",
                event.getInventoryItemId().value(), event.getRoomId(), e.getMessage(), e);
            throw new RuntimeException("아이템 배치 이벤트 처리 실패: " + e.getMessage(), e);
        }
    }
}