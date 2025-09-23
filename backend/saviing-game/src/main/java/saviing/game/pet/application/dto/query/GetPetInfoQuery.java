package saviing.game.pet.application.dto.query;

import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 펫 정보 조회 Query
 */
public record GetPetInfoQuery(
    InventoryItemId inventoryItemId,
    CharacterId characterId
) {
    /**
     * GetPetInfoQuery를 생성합니다.
     *
     * @param petId 펫 ID (API에서 받은 값, 실제로는 inventoryItemId)
     * @param characterId 캐릭터 ID
     * @return GetPetInfoQuery
     */
    public static GetPetInfoQuery of(Long petId, Long characterId) {
        return new GetPetInfoQuery(
            InventoryItemId.of(petId),
            CharacterId.of(characterId)
        );
    }
}