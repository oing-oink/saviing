package saviing.game.character.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CustomerId;

/**
 * 캐릭터 생성 Command입니다.
 *
 * @param customerId 고객 ID
 */
@Builder
public record CreateCharacterCommand(
    CustomerId customerId
) {
    /**
     * CreateCharacterCommand를 생성합니다.
     *
     * @param customerId 고객 ID
     * @return CreateCharacterCommand 인스턴스
     */
    public static CreateCharacterCommand of(CustomerId customerId) {
        return CreateCharacterCommand.builder()
            .customerId(customerId)
            .build();
    }
}