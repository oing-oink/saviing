package saviing.game.item.application.dto.query;

import lombok.Builder;

/**
 * 단일 아이템 조회 쿼리 DTO
 */
@Builder
public record GetItemQuery(
    Long itemId
) {
}