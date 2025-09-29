package saviing.game.inventory.application.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import saviing.game.inventory.application.dto.command.EquipAccessoryCommand;
import saviing.game.inventory.application.service.InventoryCommandService;
import saviing.game.inventory.domain.event.AccessoryEquippedEvent;

/**
 * 액세서리 장착/해제 이벤트 핸들러
 * Pet 도메인에서 발행하는 AccessoryEquippedEvent를 동기적으로 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessoryEquippedEventHandler {

    private final InventoryCommandService inventoryCommandService;

    /**
     * 액세서리 장착/해제 이벤트를 처리합니다.
     * Pet 도메인에서 액세서리 장착/해제 완료 시 inventory의 장착 정보를 업데이트합니다.
     *
     * @param event 액세서리 장착/해제 이벤트
     */
    @EventListener
    public void handleAccessoryEquippedEvent(AccessoryEquippedEvent event) {
        log.info("Processing AccessoryEquippedEvent: accessoryInventoryItemId={}, petInventoryItemId={}, equipped={}",
            event.getAccessoryInventoryItemId().value(),
            event.getPetInventoryItemId() != null ? event.getPetInventoryItemId().value() : null,
            event.isEquipped());

        try {
            EquipAccessoryCommand command;
            if (event.isEquipped()) {
                command = EquipAccessoryCommand.equip(
                    event.getAccessoryInventoryItemId(),
                    event.getPetInventoryItemId(),
                    event.getCharacterId()
                );
            } else {
                command = EquipAccessoryCommand.unequip(
                    event.getAccessoryInventoryItemId(),
                    event.getCharacterId()
                );
            }

            inventoryCommandService.equipAccessory(command);

            log.info("Successfully processed AccessoryEquippedEvent: accessoryInventoryItemId={}, equipped={}",
                event.getAccessoryInventoryItemId().value(), event.isEquipped());

        } catch (Exception e) {
            log.error("Failed to process AccessoryEquippedEvent: accessoryInventoryItemId={}, equipped={}, error={}",
                event.getAccessoryInventoryItemId().value(), event.isEquipped(), e.getMessage(), e);
            throw new RuntimeException("액세서리 장착/해제 이벤트 처리 실패: " + e.getMessage(), e);
        }
    }
}