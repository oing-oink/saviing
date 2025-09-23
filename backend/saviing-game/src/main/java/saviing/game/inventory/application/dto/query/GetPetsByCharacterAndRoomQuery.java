package saviing.game.inventory.application.dto.query;

import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 캐릭터와 방 ID별 펫 목록 조회 Query
 */
public record GetPetsByCharacterAndRoomQuery(
    CharacterId characterId,
    Long roomId
) {
    /**
     * GetPetsByCharacterAndRoomQuery를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param roomId 방 ID
     * @return GetPetsByCharacterAndRoomQuery
     */
    public static GetPetsByCharacterAndRoomQuery of(Long characterId, Long roomId) {
        return new GetPetsByCharacterAndRoomQuery(CharacterId.of(characterId), roomId);
    }
}