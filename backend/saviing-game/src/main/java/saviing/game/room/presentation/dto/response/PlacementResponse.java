package saviing.game.room.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 배치 응답 DTO입니다.
 */
@Builder
@Schema(description = "배치 아이템 응답")
public record PlacementResponse(
    @Schema(description = "배치 식별자", example = "1")
    @JsonProperty("placementId")
    Long placementId,

    @Schema(description = "인벤토리 아이템 식별자", example = "101")
    @JsonProperty("inventoryItemId")
    Long inventoryItemId,

    @Schema(description = "아이템 식별자", example = "201")
    @JsonProperty("itemId")
    Long itemId,

    @Schema(description = "X 좌표", example = "2")
    @JsonProperty("positionX")
    Integer positionX,

    @Schema(description = "Y 좌표", example = "3")
    @JsonProperty("positionY")
    Integer positionY,

    @Schema(description = "X축 길이 (가로)", example = "2")
    @JsonProperty("xLength")
    Integer xLength,

    @Schema(description = "Y축 길이 (세로)", example = "1")
    @JsonProperty("yLength")
    Integer yLength,

    @Schema(description = "아이템 카테고리", example = "FURNITURE", allowableValues = {"FURNITURE", "PET", "DECORATION"})
    @JsonProperty("category")
    String category
) {
    /**
     * PlacementResponse 생성자입니다.
     *
     * @param placementId 배치 식별자
     * @param inventoryItemId 인벤토리 아이템 식별자
     * @param itemId 아이템 식별자
     * @param positionX X 좌표
     * @param positionY Y 좌표
     * @param xLength X축 길이
     * @param yLength Y축 길이
     * @param category 카테고리
     * @throws IllegalArgumentException 필수 값이 null인 경우
     */
    public PlacementResponse {
        if (placementId == null) {
            throw new IllegalArgumentException("placementId는 필수입니다");
        }
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