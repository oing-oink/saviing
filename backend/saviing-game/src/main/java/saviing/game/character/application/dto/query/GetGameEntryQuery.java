package saviing.game.character.application.dto.query;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CustomerId;

/**
 * 메인 엔트리 게임 정보 조회 Query
 */
@Builder
public record GetGameEntryQuery(
    CustomerId customerId
) {
}