package saviing.game.room.application.dto.command;

import java.util.List;

import lombok.Builder;

/**
 * 방 배치 저장 명령 DTO입니다.
 */
@Builder
public record SaveRoomPlacementsCommand(
    Long roomId,
    Long characterId,
    List<PlaceItemCommand> placedItems
) {
    /**
     * SaveRoomPlacementsCommand 생성자입니다.
     *
     * @param roomId 방 식별자
     * @param characterId 캐릭터 식별자
     * @param placedItems 배치할 아이템 목록
     * @throws IllegalArgumentException 필수 값이 null인 경우
     */
    public SaveRoomPlacementsCommand {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId는 필수입니다");
        }
        if (characterId == null) {
            throw new IllegalArgumentException("characterId는 필수입니다");
        }
        if (placedItems == null) {
            throw new IllegalArgumentException("placedItems는 필수입니다");
        }
    }

    /**
     * 명령의 유효성을 검증합니다.
     *
     * @throws IllegalArgumentException 유효하지 않은 데이터가 포함된 경우
     */
    public void validate() {
        if (roomId <= 0) {
            throw new IllegalArgumentException("roomId는 양수여야 합니다");
        }
        if (characterId <= 0) {
            throw new IllegalArgumentException("characterId는 양수여야 합니다");
        }

        for (PlaceItemCommand command : placedItems) {
            if (command == null) {
                throw new IllegalArgumentException("placedItems에 null 요소가 포함되어 있습니다");
            }
        }
    }
}