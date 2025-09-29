package saviing.game.room.domain.model.vo;

import lombok.NonNull;

/**
 * 배치(Placement) 식별자를 나타내는 값 객체
 *
 * @param value 배치 식별자 값. 양수여야 함
 */
public record PlacementId(@NonNull Long value) {

    /**
     * PlacementId 생성자
     *
     * @param value 배치 식별자 값
     * @throws IllegalArgumentException value가 null이거나 0 이하인 경우
     */
    public PlacementId {
        if (value <= 0) {
            throw new IllegalArgumentException("placementId must be positive");
        }
    }
}