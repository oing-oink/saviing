package saviing.game.pet.application.dto.query;

import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 펫 정보 조회 Query
 */
public record GetPetInfoQuery(
    InventoryItemId inventoryItemId
) {
    /**
     * GetPetInfoQuery를 생성합니다.
     *
     * @param petId 펫 ID (API에서 받은 값, 실제로는 inventoryItemId)
     * @return GetPetInfoQuery
     */
    public static GetPetInfoQuery of(Long petId) {
        return new GetPetInfoQuery(
            InventoryItemId.of(petId)
        );
    }
}