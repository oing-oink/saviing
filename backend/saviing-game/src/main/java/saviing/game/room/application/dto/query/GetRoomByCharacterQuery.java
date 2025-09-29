package saviing.game.room.application.dto.query;

import lombok.Builder;

/**
 * 캐릭터별 방 조회 쿼리 DTO
 * 특정 캐릭터의 특정 번호 방을 조회하기 위한 요청 데이터를 담음
 */
@Builder
public record GetRoomByCharacterQuery(
    Long characterId,
    Integer roomNumber
) {
    /**
     * GetRoomByCharacterQuery 생성자
     * 필수 파라미터의 유효성을 검증
     *
     * @param characterId 조회할 캐릭터의 식별자
     * @param roomNumber 조회할 방 번호
     * @throws IllegalArgumentException characterId가 null이거나 0 이하인 경우
     * @throws IllegalArgumentException roomNumber가 null이거나 0 이하인 경우
     */
    public GetRoomByCharacterQuery {
        if (characterId == null || characterId <= 0) {
            throw new IllegalArgumentException("characterId는 양수여야 합니다");
        }
        if (roomNumber == null || roomNumber < 1 || roomNumber > 5) {
            throw new IllegalArgumentException("유효한 roomNumber가 아닙니다");
        }
    }
}