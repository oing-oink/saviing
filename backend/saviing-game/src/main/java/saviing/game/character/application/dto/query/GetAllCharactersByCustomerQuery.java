package saviing.game.character.application.dto.query;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CustomerId;

/**
 * 고객의 모든 캐릭터 조회 Query
 */
@Builder
public record GetAllCharactersByCustomerQuery(
    CustomerId customerId
) {
    /**
     * GetAllCharactersByCustomerQuery를 생성합니다.
     *
     * @param customerId 고객 ID
     * @return GetAllCharactersByCustomerQuery
     */
    public static GetAllCharactersByCustomerQuery of(Long customerId) {
        return new GetAllCharactersByCustomerQuery(new CustomerId(customerId));
    }
}