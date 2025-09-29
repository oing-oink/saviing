package saviing.game.character.application.dto.query;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CustomerId;

/**
 * 활성 캐릭터 조회 Query
 */
@Builder
public record GetActiveCharacterQuery(
    CustomerId customerId
) {
    /**
     * GetActiveCharacterQuery를 생성합니다.
     *
     * @param customerId 고객 ID
     * @return GetActiveCharacterQuery
     */
    public static GetActiveCharacterQuery of(Long customerId) {
        return new GetActiveCharacterQuery(new CustomerId(customerId));
    }
}