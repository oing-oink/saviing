package saviing.game.room.presentation.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 방 정보 응답 DTO입니다.
 */
@Builder
@Schema(description = "방 정보 응답")
public record RoomResponse(
    @Schema(description = "방 식별자", example = "1")
    @JsonProperty("roomId")
    Long roomId,

    @Schema(description = "캐릭터 식별자", example = "1")
    @JsonProperty("characterId")
    Long characterId,

    @Schema(description = "방 번호", example = "1")
    @JsonProperty("roomNumber")
    Byte roomNumber,

    @Schema(description = "생성 시각", example = "2023-12-01T10:00:00")
    @JsonProperty("createdAt")
    LocalDateTime createdAt,

    @Schema(description = "수정 시각", example = "2023-12-01T10:00:00")
    @JsonProperty("updatedAt")
    LocalDateTime updatedAt
) {
    /**
     * RoomResponse 생성자입니다.
     *
     * @param roomId 방 식별자
     * @param characterId 캐릭터 식별자
     * @param roomNumber 방 번호
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @throws IllegalArgumentException 필수 값이 null인 경우
     */
    public RoomResponse {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId는 필수입니다");
        }
        if (characterId == null) {
            throw new IllegalArgumentException("characterId는 필수입니다");
        }
        if (roomNumber == null) {
            throw new IllegalArgumentException("roomNumber는 필수입니다");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt는 필수입니다");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt는 필수입니다");
        }
    }
}