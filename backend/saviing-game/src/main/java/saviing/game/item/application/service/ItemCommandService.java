package saviing.game.item.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.item.application.dto.command.ChangeAvailabilityCommand;
import saviing.game.item.application.dto.command.RegisterItemCommand;
import saviing.game.item.application.dto.command.UpdateItemCommand;
import saviing.game.item.application.dto.result.ItemResult;
import saviing.game.item.application.dto.result.VoidResult;
import saviing.game.item.application.mapper.ItemResultMapper;
import saviing.game.item.domain.exception.InvalidItemDefinitionException;
import saviing.game.item.domain.exception.ItemNotFoundException;
import saviing.game.item.domain.model.aggregate.Item;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.vo.*;
import saviing.game.item.domain.repository.ItemRepository;

/**
 * 아이템 명령 처리 서비스
 * 아이템의 생성, 수정, 삭제 등의 작업을 처리합니다.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemCommandService {

    private final ItemRepository itemRepository;
    private final ItemResultMapper itemResultMapper;

    /**
     * 새로운 아이템을 등록합니다.
     *
     * @param command 아이템 등록 명령
     * @return 등록된 아이템 결과
     */
    public ItemResult registerItem(RegisterItemCommand command) {
        log.info("아이템 등록 시작: {}", command.itemName());

        Item item = Item.create(
            ItemName.of(command.itemName()),
            ItemDescription.of(command.itemDescription()),
            command.itemType(),
            command.itemCategory(),
            command.rarity(),
            createItemSize(command.itemType(), command.xLength(), command.yLength()),
            Price.of(command.coin(), command.fishCoin()),
            ImageUrl.of(command.imageUrl())
        );

        Item savedItem = itemRepository.save(item);

        log.info("아이템 등록 완료: ID={}", savedItem.getItemId().value());
        return itemResultMapper.toResult(savedItem);
    }

    /**
     * 기존 아이템을 수정합니다.
     *
     * @param command 아이템 수정 명령
     * @return 수정된 아이템 결과
     */
    public ItemResult updateItem(UpdateItemCommand command) {
        log.info("아이템 수정 시작: ID={}", command.itemId());

        ItemId itemId = ItemId.of(command.itemId());
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> ItemNotFoundException.withItemId(itemId));

        // 아이템 정보 업데이트
        if (command.itemName() != null) {
            item.updateName(ItemName.of(command.itemName()));
        }
        if (command.itemDescription() != null) {
            item.updateDescription(ItemDescription.of(command.itemDescription()));
        }
        if (command.rarity() != null) {
            item.updateRarity(command.rarity());
        }
        if (command.coin() != null || command.fishCoin() != null) {
            item.updatePrice(Price.of(command.coin(), command.fishCoin()));
        }
        if (command.imageUrl() != null) {
            item.updateImageUrl(ImageUrl.of(command.imageUrl()));
        }

        Item savedItem = itemRepository.save(item);

        log.info("아이템 수정 완료: ID={}", savedItem.getItemId().value());
        return itemResultMapper.toResult(savedItem);
    }

    /**
     * 아이템의 가용성을 변경합니다.
     *
     * @param command 가용성 변경 명령
     * @return VoidResult
     */
    public VoidResult changeAvailability(ChangeAvailabilityCommand command) {
        log.info("아이템 가용성 변경 시작: ID={}, available={}", command.itemId(), command.isAvailable());

        ItemId itemId = ItemId.of(command.itemId());
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> ItemNotFoundException.withItemId(itemId));

        if (command.isAvailable()) {
            item.makeAvailable();
        } else {
            item.makeUnavailable(command.reason());
        }

        itemRepository.save(item);

        log.info("아이템 가용성 변경 완료: ID={}", itemId.value());
        return VoidResult.of();
    }

    /**
     * 아이템을 삭제합니다.
     *
     * @param itemId 삭제할 아이템 ID
     * @return VoidResult
     */
    public VoidResult deleteItem(Long itemId) {
        log.info("아이템 삭제 시작: ID={}", itemId);

        ItemId id = ItemId.of(itemId);
        if (!itemRepository.existsById(id)) {
            throw ItemNotFoundException.withItemId(id);
        }

        itemRepository.deleteById(id);

        log.info("아이템 삭제 완료: ID={}", itemId);
        return VoidResult.of();
    }

    /**
     * 아이템 타입에 따라 적절한 ItemSize를 생성합니다.
     *
     * @param itemType 아이템 타입
     * @param xLength X 길이
     * @param yLength Y 길이
     * @return 타입에 맞는 ItemSize
     */
    private ItemSize createItemSize(ItemType itemType, Integer xLength, Integer yLength) {
        return switch (itemType) {
            case DECORATION -> {
                if (xLength == null || yLength == null) {
                    throw InvalidItemDefinitionException.decorationSizeRequired();
                }
                yield ItemSize.required(xLength, yLength);
            }
            case PET, ACCESSORY -> ItemSize.optional();
        };
    }
}