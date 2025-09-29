package saviing.game.inventory.presentation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import saviing.game.inventory.application.dto.query.GetInventoriesByCharacterQuery;
import saviing.game.inventory.domain.model.enums.InventoryType;
import saviing.game.inventory.domain.model.enums.ItemCategory;

/**
 * Inventory Presentation layer Request를 Application layer Query로 변환하는 Mapper
 * 요청 파라미터를 적절한 Query 객체로 변환하고 검증합니다.
 */
@Component
@RequiredArgsConstructor
public class InventoryRequestMapper {

    /**
     * 요청 파라미터를 GetInventoriesByCharacterQuery로 변환합니다.
     *
     * @param characterId 캐릭터 ID
     * @param type 인벤토리 타입
     * @param category 아이템 카테고리
     * @param isUsed 사용 여부
     * @return GetInventoriesByCharacterQuery
     */
    public GetInventoriesByCharacterQuery toQuery(
        Long characterId, InventoryType type, ItemCategory category, Boolean isUsed
    ) {
        validateTypeAndCategory(type, category);

        return GetInventoriesByCharacterQuery.of(characterId, type, category, isUsed);
    }

    /**
     * 타입과 카테고리의 조합이 유효한지 검증합니다.
     *
     * @param type 인벤토리 타입
     * @param category 아이템 카테고리
     * @throws IllegalArgumentException 타입과 카테고리 조합이 유효하지 않은 경우
     */
    private void validateTypeAndCategory(InventoryType type, ItemCategory category) {
        // 둘 다 null이거나 둘 중 하나만 null인 경우는 허용
        if (type == null || category == null) {
            return;
        }

        // 카테고리가 해당 타입에 속하는지 검증
        if (!category.belongsTo(type)) {
            throw new IllegalArgumentException(
                String.format("카테고리 %s는 타입 %s에 속하지 않습니다. %s 타입에서 사용 가능한 카테고리: %s",
                    category.name(), type.name(), type.name(),
                    java.util.Arrays.toString(ItemCategory.getByInventoryType(type)))
            );
        }
    }
}