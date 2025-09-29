package saviing.game.room.application.dto.query;

import lombok.Builder;

/**
 * 방 배치 조회 쿼리 DTO
 * 특정 룸의 배치 정보를 조회하기 위한 요청 데이터를 담음
 */
@Builder
public record GetRoomPlacementsQuery(
    Long roomId
) {
    /**
     * GetRoomPlacementsQuery 생성자
     * 필수 파라미터의 유효성을 검증
     *
     * @param roomId 조회할 방의 식별자
     * @throws IllegalArgumentException roomId가 null이거나 0 이하인 경우
     */
    public GetRoomPlacementsQuery {
        if (roomId == null || roomId <= 0) {
            throw new IllegalArgumentException("roomId는 양수여야 합니다");
        }
    }

    /**
     * 쿼리의 유효성을 검증
     * 현재는 생성자에서 모든 검증을 수행하므로 추가 검증 없음
     *
     * @throws IllegalArgumentException 유효하지 않은 데이터가 포함된 경우
     */
    public void validate() {
        // 생성자에서 이미 검증되었으므로 추가 검증 없음
    }
}