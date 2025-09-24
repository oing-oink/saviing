package saviing.game.room.presentation.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 방 배치 목록 응답 DTO입니다.
 */
@Builder
@Schema(description = "방 배치 목록 응답")
public record RoomPlacementsResponse(
    @Schema(description = "방 식별자", example = "1")
    @JsonProperty("roomId")
    Long roomId,

    @Schema(description = "배치된 아이템 목록")
    @JsonProperty("placements")
    List<PlacementResponse> placements
) {
    /**
     * RoomPlacementsResponse 생성자입니다.
     *
     * @param roomId 방 식별자
     * @param placements 배치 응답 목록
     * @throws IllegalArgumentException 필수 값이 null인 경우
     */
    public RoomPlacementsResponse {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId는 필수입니다");
        }
        if (placements == null) {
            throw new IllegalArgumentException("placements는 필수입니다");
        }
    }
}