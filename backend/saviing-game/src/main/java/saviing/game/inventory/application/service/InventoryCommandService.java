package saviing.game.inventory.application.service;

import saviing.game.inventory.application.dto.command.AddInventoryItemCommand;
import saviing.game.inventory.application.dto.command.ConsumeInventoryItemCommand;
import saviing.game.inventory.application.dto.command.EquipAccessoryCommand;
import saviing.game.inventory.application.dto.command.PlaceInventoryItemsCommand;
import saviing.game.inventory.application.dto.result.InventoryAddedResult;
import saviing.game.inventory.application.dto.result.VoidResult;
import saviing.game.inventory.domain.exception.InventoryItemNotFoundException;
import saviing.game.inventory.domain.model.aggregate.AccessoryInventory;
import saviing.game.inventory.domain.model.aggregate.ConsumptionInventory;
import saviing.game.inventory.domain.model.aggregate.DecorationInventory;
import saviing.game.inventory.domain.model.aggregate.Inventory;
import saviing.game.inventory.domain.model.aggregate.PetInventory;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.inventory.domain.repository.InventoryRepository;
import saviing.game.item.domain.exception.ItemNotFoundException;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.domain.model.enums.Accessory;
import saviing.game.item.domain.model.enums.Consumption;
import saviing.game.item.domain.model.enums.Decoration;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Pet;
import saviing.game.item.domain.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.inventory.domain.event.ItemPurchasedEvent;

import java.util.List;
import java.util.Optional;

/**
 * 인벤토리 Command를 처리하는 서비스입니다.
 * 인벤토리는 아이템의 보유 여부와 사용 상태만을 관리하고, 실제 비즈니스 동작은 다른 도메인(펫, 룸 등)에 위임합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InventoryCommandService {

    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 인벤토리에 새 아이템을 추가합니다.
     * 소모품의 경우 개수 기반으로 관리합니다.
     */
    @Transactional
    public InventoryAddedResult addInventoryItem(AddInventoryItemCommand command) {
        log.info("Adding inventory item for character: {}, itemId: {}, count: {}",
            command.characterId().value(), command.itemId().value(), command.count());

        Item item = itemRepository.findById(command.itemId())
            .orElseThrow(() -> ItemNotFoundException.withItemId(command.itemId()));

        InventoryAddedResult result = switch (item.getItemType()) {
            case PET -> createPetInventory(command, item);
            case ACCESSORY -> createAccessoryInventory(command, item);
            case DECORATION -> createDecorationInventory(command, item);
            case CONSUMPTION -> handleConsumptionInventory(command, item);
        };

        // PET 아이템일 때만 이벤트 발행
        if (item.getItemType() == ItemType.PET) {
            eventPublisher.publishEvent(ItemPurchasedEvent.of(
                command.characterId(),
                command.itemId(),
                InventoryItemId.of(result.inventoryItemId()),
                item.getItemName().value()
            ));
            log.info("Published ItemPurchasedEvent for PET item: characterId={}, itemId={}, inventoryItemId={}",
                command.characterId().value(), command.itemId().value(), result.inventoryItemId());
        }

        return result;
    }

    /**
     * 펫 인벤토리를 생성합니다.
     */
    private InventoryAddedResult createPetInventory(AddInventoryItemCommand command, Item item) {
        PetInventory inventory = PetInventory.create(command.characterId(), command.itemId(), (Pet) item.getItemCategory());
        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryAddedResult.of(savedInventory.getInventoryItemId().value());
    }

    /**
     * 액세서리 인벤토리를 생성합니다.
     */
    private InventoryAddedResult createAccessoryInventory(AddInventoryItemCommand command, Item item) {
        AccessoryInventory inventory = AccessoryInventory.create(command.characterId(), command.itemId(), (Accessory) item.getItemCategory());
        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryAddedResult.of(savedInventory.getInventoryItemId().value());
    }

    /**
     * 데코레이션 인벤토리를 생성합니다.
     */
    private InventoryAddedResult createDecorationInventory(AddInventoryItemCommand command, Item item) {
        DecorationInventory inventory = DecorationInventory.create(command.characterId(), command.itemId(), (Decoration) item.getItemCategory());
        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryAddedResult.of(savedInventory.getInventoryItemId().value());
    }

    /**
     * 소모품 인벤토리를 처리합니다. (기존 updateConsumptionCount 로직 통합)
     */
    private InventoryAddedResult handleConsumptionInventory(AddInventoryItemCommand command, Item item) {
        Integer count = command.count() != null ? command.count() : 1;

        // itemId로 기존 소모품 인벤토리 조회
        Optional<ConsumptionInventory> existingConsumption = inventoryRepository
            .findConsumptionByCharacterIdAndItemId(command.characterId(), command.itemId());

        if (existingConsumption.isPresent()) {
            // 기존 소모품이 있으면 개수 증가
            ConsumptionInventory consumption = existingConsumption.get();
            consumption.increaseCount(count);
            ConsumptionInventory savedConsumption = (ConsumptionInventory) inventoryRepository.save(consumption);
            return InventoryAddedResult.of(savedConsumption.getInventoryItemId().value());
        } else {
            // 기존 소모품이 없으면 새로 생성
            ConsumptionInventory newConsumption = ConsumptionInventory.create(
                command.characterId(),
                command.itemId(),
                (Consumption) item.getItemCategory()
            );

            // 1개 이상인 경우 추가로 증가시킴
            if (count > 1) {
                newConsumption.increaseCount(count - 1);
            }

            ConsumptionInventory savedConsumption = (ConsumptionInventory) inventoryRepository.save(newConsumption);
            return InventoryAddedResult.of(savedConsumption.getInventoryItemId().value());
        }
    }

    /**
     * 소모품 아이템의 개수를 증가시키거나 감소시킵니다.
     * Pet 도메인에서 소모품 사용 또는 아이템 획득 시 호출됩니다.
     */
    @Transactional
    public VoidResult consumeInventoryItem(ConsumeInventoryItemCommand command) {
        log.info("Updating consumption inventory item: {}, quantity change: {}",
            command.inventoryItemId().value(), command.quantityChange());

        command.validate();

        Inventory inventory = inventoryRepository.findById(command.inventoryItemId())
            .orElseThrow(() -> new InventoryItemNotFoundException(command.inventoryItemId()));

        if (!(inventory instanceof ConsumptionInventory consumption)) {
            throw new IllegalArgumentException("소모품 인벤토리가 아닙니다: " + inventory.getType());
        }

        if (command.isIncrease()) {
            consumption.increaseCount(command.quantityChange());
        } else {
            consumption.decreaseCount(Math.abs(command.quantityChange()));
        }

        inventoryRepository.save(consumption);

        return VoidResult.of();
    }


    /**
     * 여러 아이템을 방에 한 번에 배치합니다.
     * Room 도메인에서 다중 아이템 배치 시 호출됩니다.
     */
    @Transactional
    public VoidResult placeInventoryItems(PlaceInventoryItemsCommand command) {
        log.info("Placing multiple inventory items: {} in room: {}",
            command.inventoryItemIds().size(), command.roomId());

        command.validate();

        for (InventoryItemId inventoryItemId : command.inventoryItemIds()) {
            Inventory inventory = inventoryRepository.findById(inventoryItemId)
                .orElseThrow(() -> new InventoryItemNotFoundException(inventoryItemId));

            // 펫과 데코레이션 모두 방 배치 처리
            if (inventory instanceof PetInventory petInventory) {
                petInventory.placeInRoom(command.roomId());
                inventoryRepository.save(petInventory);
            } else if (inventory instanceof DecorationInventory decorationInventory) {
                // TODO: DecorationInventory에 placeInRoom 메서드 추가 필요
                // decorationInventory.placeInRoom(command.roomId());
                // inventoryRepository.save(decorationInventory);
                log.warn("Decoration placement not yet implemented for item: {}", inventoryItemId.value());
            }
        }

        return VoidResult.of();
    }

    /**
     * 액세서리를 펫에게 장착하거나 해제합니다.
     * Pet 도메인에서 액세서리 장착/해제 시 호출됩니다.
     */
    @Transactional
    public VoidResult equipAccessory(EquipAccessoryCommand command) {
        log.info("Equipping accessory: {} to pet: {}, equip: {}",
            command.accessoryInventoryItemId().value(),
            command.petInventoryItemId() != null ? command.petInventoryItemId().value() : null,
            command.equip());

        command.validate();

        AccessoryInventory accessory = inventoryRepository.findAccessoryById(command.accessoryInventoryItemId())
            .orElseThrow(() -> new InventoryItemNotFoundException(command.accessoryInventoryItemId()));

        if (command.equip()) {
            accessory.equipToPet(command.petInventoryItemId());
        } else {
            accessory.unequipFromPet();
        }

        inventoryRepository.save(accessory);

        return VoidResult.of();
    }

    /**
     * 특정 방에 배치된 모든 데코레이션 인벤토리의 사용 상태를 해제합니다.
     * Room BC에서 방 배치 초기화 시 호출됩니다.
     *
     * @param roomId 방 식별자
     * @throws IllegalArgumentException roomId가 null이거나 0 이하인 경우
     */
    @Transactional
    public void resetRoomUsage(Long roomId) {
        if (roomId == null || roomId <= 0) {
            throw new IllegalArgumentException("roomId는 필수이며 양수여야 합니다");
        }

        log.info("Resetting room usage for roomId: {}", roomId);

        inventoryRepository.updateRoomUsageToFalse(roomId);

        log.debug("Room usage reset completed for roomId: {}", roomId);
    }

    /**
     * 지정된 인벤토리 아이템들을 사용 중 상태로 표시하고 특정 방에 배치합니다.
     * Room BC에서 방 배치 완료 시 호출됩니다.
     *
     * @param inventoryItemIds 사용 중으로 표시할 인벤토리 아이템 ID 목록
     * @param roomId 배치할 방의 식별자
     * @throws IllegalArgumentException inventoryItemIds가 null이거나 비어있는 경우
     * @throws IllegalArgumentException roomId가 null이거나 0 이하인 경우
     * @throws InventoryItemNotFoundException 존재하지 않는 인벤토리 아이템이 포함된 경우
     */
    @Transactional
    public void markAsUsed(List<Long> inventoryItemIds, Long roomId) {
        if (inventoryItemIds == null || inventoryItemIds.isEmpty()) {
            log.debug("No inventory items to mark as used");
            return;
        }
        if (roomId == null || roomId <= 0) {
            throw new IllegalArgumentException("roomId는 필수이며 양수여야 합니다");
        }

        log.info("Marking {} inventory items as used in room {}", inventoryItemIds.size(), roomId);

        inventoryRepository.updateUsageToTrue(inventoryItemIds, roomId);

        log.debug("Successfully marked {} items as used in room {}", inventoryItemIds.size(), roomId);
    }

}