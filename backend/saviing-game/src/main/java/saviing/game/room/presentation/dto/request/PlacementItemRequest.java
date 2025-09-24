package saviing.game.room.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * 배치 아이템 요청 DTO입니다.
 */
@Builder
public record PlacementItemRequest(
    @JsonProperty("inventoryItemId")
    Long inventoryItemId,

    @JsonProperty("itemId")
    Long itemId,

    @JsonProperty("positionX")
    Integer positionX,

    @JsonProperty("positionY")
    Integer positionY,

    @JsonProperty("xLength")
    Integer xLength,

    @JsonProperty("yLength")
    Integer yLength,

    @JsonProperty("category")
    String category
) {
    /**
     * PlacementItemRequest 생성자입니다.
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
    public PlacementItemRequest {
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
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("category는 필수입니다");
        }
    }
}