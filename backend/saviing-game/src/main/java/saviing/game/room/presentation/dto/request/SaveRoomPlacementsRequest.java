package saviing.game.room.presentation.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 방 배치 저장 요청 DTO입니다.
 */
@Builder
@Schema(description = "방 배치 저장 요청")
public record SaveRoomPlacementsRequest(
    @Schema(description = "캐릭터 식별자", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("characterId")
    Long characterId,

    @Schema(description = "배치할 아이템 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("placedItems")
    List<PlacementItemRequest> placedItems
) {
    /**
     * SaveRoomPlacementsRequest 생성자입니다.
     *
     * @param characterId 캐릭터 식별자
     * @param placedItems 배치할 아이템 목록
     * @throws IllegalArgumentException 필수 값이 null인 경우
     */
    public SaveRoomPlacementsRequest {
        if (characterId == null) {
            throw new IllegalArgumentException("characterId는 필수입니다");
        }
        if (placedItems == null) {
            throw new IllegalArgumentException("placedItems는 필수입니다");
        }
    }
}