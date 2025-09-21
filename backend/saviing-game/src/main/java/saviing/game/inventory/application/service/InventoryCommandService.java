package saviing.game.inventory.application.service;

import saviing.common.event.DomainEventPublisher;
import saviing.game.inventory.application.dto.command.AddInventoryItemCommand;
import saviing.game.inventory.application.dto.command.ConsumeInventoryItemCommand;
import saviing.game.inventory.application.dto.command.EquipAccessoryCommand;
import saviing.game.inventory.application.dto.command.PlaceInventoryItemCommand;
import saviing.game.inventory.application.dto.command.UpdateConsumptionCountCommand;
import saviing.game.inventory.application.dto.result.InventoryAddedResult;
import saviing.game.inventory.application.dto.result.VoidResult;
import saviing.game.inventory.domain.exception.InventoryItemNotFoundException;
import saviing.game.inventory.domain.model.aggregate.AccessoryInventory;
import saviing.game.inventory.domain.model.aggregate.ConsumptionInventory;
import saviing.game.inventory.domain.model.aggregate.DecorationInventory;
import saviing.game.inventory.domain.model.aggregate.Inventory;
import saviing.game.inventory.domain.model.aggregate.PetInventory;
import saviing.game.inventory.domain.repository.InventoryRepository;
import saviing.game.item.domain.exception.ItemNotFoundException;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.domain.model.enums.Accessory;
import saviing.game.item.domain.model.enums.Consumption;
import saviing.game.item.domain.model.enums.Decoration;
import saviing.game.item.domain.model.enums.Pet;
import saviing.game.item.domain.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DomainEventPublisher domainEventPublisher;

    /**
     * 인벤토리에 새 아이템을 추가합니다.
     */
    @Transactional
    public InventoryAddedResult addInventoryItem(AddInventoryItemCommand command) {
        log.info("Adding inventory item for character: {}, itemId: {}",
            command.characterId().value(), command.itemId().value());

        Item item = itemRepository.findById(command.itemId())
            .orElseThrow(() -> ItemNotFoundException.withItemId(command.itemId()));

        Inventory inventory = switch (item.getItemType()) {
            case PET -> PetInventory.create(command.characterId(), command.itemId(), (Pet) item.getItemCategory());
            case ACCESSORY -> AccessoryInventory.create(command.characterId(), command.itemId(), (Accessory) item.getItemCategory());
            case DECORATION -> DecorationInventory.create(command.characterId(), command.itemId(), (Decoration) item.getItemCategory());
            case CONSUMPTION -> ConsumptionInventory.create(command.characterId(), command.itemId(), (Consumption) item.getItemCategory());
        };
        Inventory savedInventory = inventoryRepository.save(inventory);

        savedInventory.getDomainEvents().forEach(domainEventPublisher::publish);

        return InventoryAddedResult.of(savedInventory.getInventoryItemId().value());
    }

    /**
     * 소모품 아이템의 개수를 감소시킵니다.
     * Pet 도메인에서 소모품 사용 시 호출됩니다.
     */
    @Transactional
    public VoidResult consumeInventoryItem(ConsumeInventoryItemCommand command) {
        log.info("Consuming inventory item: {}, quantity: {}",
            command.inventoryItemId().value(), command.consumedQuantity());

        command.validate();

        Inventory inventory = inventoryRepository.findById(command.inventoryItemId())
            .orElseThrow(() -> new InventoryItemNotFoundException(command.inventoryItemId()));

        // 소모품 사용 로직 (현재는 단순히 이벤트만 발행)
        // 실제 소모품 처리는 별도 도메인에서 관리
        inventory.getDomainEvents().forEach(domainEventPublisher::publish);

        return VoidResult.of();
    }

    /**
     * 아이템을 방에 배치합니다.
     * Room 도메인에서 아이템 배치 시 호출됩니다.
     */
    @Transactional
    public VoidResult placeInventoryItem(PlaceInventoryItemCommand command) {
        log.info("Placing inventory item: {} in room: {}",
            command.inventoryItemId().value(), command.roomId());

        command.validate();

        Inventory inventory = inventoryRepository.findById(command.inventoryItemId())
            .orElseThrow(() -> new InventoryItemNotFoundException(command.inventoryItemId()));

        // 펫인 경우에만 방 배치 처리
        if (inventory instanceof PetInventory petInventory) {
            petInventory.placeInRoom(command.roomId());
            inventoryRepository.save(petInventory);

            petInventory.getDomainEvents().forEach(domainEventPublisher::publish);
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
        accessory.getDomainEvents().forEach(domainEventPublisher::publish);

        return VoidResult.of();
    }

    /**
     * 소모품의 개수를 업데이트합니다.
     * Pet 도메인에서 소모품 사용 또는 Shop에서 소모품 구매 시 호출됩니다.
     */
    @Transactional
    public VoidResult updateConsumptionCount(UpdateConsumptionCountCommand command) {
        log.info("Updating consumption count: characterId={}, itemId={}, countChange={}",
            command.characterId().value(), command.itemId().value(), command.countChange());

        command.validate();

        // itemId로 기존 소모품 인벤토리 조회
        Optional<ConsumptionInventory> existingConsumption = inventoryRepository
            .findConsumptionByCharacterIdAndItemId(command.characterId(), command.itemId());

        if (existingConsumption.isPresent()) {
            // 기존 소모품이 있으면 개수 업데이트
            ConsumptionInventory consumption = existingConsumption.get();
            if (command.countChange() > 0) {
                consumption.increaseCount(command.countChange());
            } else {
                consumption.decreaseCount(-command.countChange());
            }
            inventoryRepository.save(consumption);
            consumption.getDomainEvents().forEach(domainEventPublisher::publish);
        } else {
            // 기존 소모품이 없고 증가인 경우에만 새로 생성
            if (command.countChange() > 0) {
                Item item = itemRepository.findById(command.itemId())
                    .orElseThrow(() -> ItemNotFoundException.withItemId(command.itemId()));

                ConsumptionInventory newConsumption = ConsumptionInventory.create(
                    command.characterId(),
                    command.itemId(),
                    (Consumption) item.getItemCategory()
                );

                // 1개 이상 증가하는 경우 추가로 증가시킴
                if (command.countChange() > 1) {
                    newConsumption.increaseCount(command.countChange() - 1);
                }

                inventoryRepository.save(newConsumption);
                newConsumption.getDomainEvents().forEach(domainEventPublisher::publish);
            } else {
                throw new IllegalArgumentException("감소할 소모품이 없습니다. itemId: " + command.itemId().value());
            }
        }

        return VoidResult.of();
    }

}