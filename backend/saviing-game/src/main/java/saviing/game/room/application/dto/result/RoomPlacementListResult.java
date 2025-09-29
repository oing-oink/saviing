package saviing.game.room.application.dto.result;

import java.util.List;

import lombok.Builder;

/**
 * 방 배치 목록 조회 결과 DTO입니다.
 */
@Builder
public record RoomPlacementListResult(
    Long roomId,
    List<PlacementInfo> placements
) {
    /**
     * RoomPlacementListResult 생성자입니다.
     *
     * @param roomId 방 식별자
     * @param placements 배치 정보 목록
     * @throws IllegalArgumentException roomId가 null이거나 placements가 null인 경우
     */
    public RoomPlacementListResult {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId는 필수입니다");
        }
        if (placements == null) {
            throw new IllegalArgumentException("placements는 필수입니다");
        }
    }

    /**
     * 배치 정보를 나타내는 내부 DTO입니다.
     */
    @Builder
    public record PlacementInfo(
        Long inventoryItemId,
        Long itemId,
        Integer positionX,
        Integer positionY,
        Integer xLength,
        Integer yLength,
        String category
    ) {
        /**
         * PlacementInfo 생성자입니다.
         *
         * @param inventoryItemId 인벤토리 아이템 식별자
         * @param itemId 아이템 식별자
         * @param positionX X 좌표
         * @param positionY Y 좌표
         * @param xLength X축 길이
         * @param yLength Y축 길이
         * @param category 카테고리
         * @throws IllegalArgumentException 필수 값이 null인 경우
         */
        public PlacementInfo {
            if (inventoryItemId == null) {
                throw new IllegalArgumentException("inventoryItemId는 필수입니다");
            }
            if (itemId == null) {
                throw new IllegalArgumentException("itemId는 필수입니다");
            }
            if (positionX == null) {
                throw new IllegalArgumentException("positionX는 필수입니다");
            }
            if (positionY == null) {
                throw new IllegalArgumentException("positionY는 필수입니다");
            }
            if (xLength == null) {
                throw new IllegalArgumentException("xLength는 필수입니다");
            }
            if (yLength == null) {
                throw new IllegalArgumentException("yLength는 필수입니다");
            }
            if (category == null) {
                throw new IllegalArgumentException("category는 필수입니다");
            }
        }
    }
}