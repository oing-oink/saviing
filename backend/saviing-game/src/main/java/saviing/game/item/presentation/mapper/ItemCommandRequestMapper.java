package saviing.game.item.presentation.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import saviing.game.item.application.dto.command.ChangeAvailabilityCommand;
import saviing.game.item.application.dto.command.RegisterItemCommand;
import saviing.game.item.application.dto.command.UpdateItemCommand;
import saviing.game.item.domain.model.enums.ItemType;
import saviing.game.item.domain.model.enums.Rarity;
import saviing.game.item.domain.model.enums.category.Category;
import saviing.game.item.presentation.dto.request.ChangeAvailabilityRequest;
import saviing.game.item.presentation.dto.request.CreateItemRequest;
import saviing.game.item.presentation.dto.request.UpdateItemRequest;

/**
 * Request DTO를 Command DTO로 변환하는 매퍼
 */
@Slf4j
@Component
public class ItemCommandRequestMapper {

    /**
     * CreateItemRequest를 RegisterItemCommand로 변환합니다.
     */
    public RegisterItemCommand toCommand(CreateItemRequest request) {
        return RegisterItemCommand.builder()
            .itemName(request.itemName())
            .itemDescription(request.itemDescription())
            .itemType(parseItemType(request.itemType()))
            .itemCategory(parseCategory(request.itemCategory()))
            .rarity(parseRarity(request.rarity()))
            .xLength(request.xLength())
            .yLength(request.yLength())
            .coin(request.coin())
            .fishCoin(request.fishCoin())
            .imageUrl(request.imageUrl())
            .build();
    }

    /**
     * UpdateItemRequest를 UpdateItemCommand로 변환합니다.
     */
    public UpdateItemCommand toCommand(Long itemId, UpdateItemRequest request) {
        return UpdateItemCommand.builder()
            .itemId(itemId)
            .itemName(request.itemName())
            .itemDescription(request.itemDescription())
            .rarity(parseRarity(request.rarity()))
            .coin(request.coin())
            .fishCoin(request.fishCoin())
            .imageUrl(request.imageUrl())
            .build();
    }

    /**
     * ChangeAvailabilityRequest를 ChangeAvailabilityCommand로 변환합니다.
     */
    public ChangeAvailabilityCommand toCommand(Long itemId, ChangeAvailabilityRequest request) {
        return ChangeAvailabilityCommand.builder()
            .itemId(itemId)
            .isAvailable(request.isAvailable())
            .reason(request.reason())
            .build();
    }

    /**
     * 문자열을 ItemType으로 변환합니다.
     */
    private ItemType parseItemType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return ItemType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 아이템 타입: {}", type);
            return null;
        }
    }

    /**
     * 문자열을 Category로 변환합니다.
     */
    private Category parseCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }

        try {
            return switch (category.toLowerCase()) {
                case "cat" -> saviing.game.item.domain.model.enums.Pet.CAT;
                case "hat" -> saviing.game.item.domain.model.enums.Accessory.HAT;
                case "left" -> saviing.game.item.domain.model.enums.Decoration.LEFT;
                case "right" -> saviing.game.item.domain.model.enums.Decoration.RIGHT;
                case "bottom" -> saviing.game.item.domain.model.enums.Decoration.BOTTOM;
                case "room_color" -> saviing.game.item.domain.model.enums.Decoration.ROOM_COLOR;
                default -> {
                    log.warn("유효하지 않은 카테고리: {}", category);
                    yield null;
                }
            };
        } catch (Exception e) {
            log.warn("카테고리 파싱 오류: {}", category, e);
            return null;
        }
    }

    /**
     * 문자열을 Rarity로 변환합니다.
     */
    private Rarity parseRarity(String rarity) {
        if (rarity == null || rarity.isBlank()) {
            return null;
        }
        try {
            return Rarity.valueOf(rarity.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 희귀도: {}", rarity);
            return null;
        }
    }
}