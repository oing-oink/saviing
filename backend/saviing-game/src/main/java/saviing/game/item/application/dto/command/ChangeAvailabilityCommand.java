package saviing.game.item.application.dto.command;

import lombok.Builder;

/**
 * 아이템 가용성 변경 명령 DTO
 */
@Builder
public record ChangeAvailabilityCommand(
    Long itemId,
    boolean isAvailable,
    String reason
) {
}