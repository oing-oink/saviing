package saviing.game.item.presentation.dto.request;

import lombok.Builder;

/**
 * 아이템 가용성 변경 요청 DTO
 */
@Builder
public record ChangeAvailabilityRequest(
    Boolean isAvailable,
    String reason
) {
}