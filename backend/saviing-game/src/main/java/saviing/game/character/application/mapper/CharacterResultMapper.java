package saviing.game.character.application.mapper;

import org.springframework.stereotype.Component;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.character.domain.model.aggregate.Character;

/**
 * Character 도메인 객체를 CharacterResult로 변환하는 Mapper
 * Application 계층에서 Domain 객체를 Result DTO로 변환합니다.
 */
@Component
public class CharacterResultMapper {

    /**
     * Character 도메인 객체를 CharacterResult로 변환합니다.
     *
     * @param character Character 도메인 객체
     * @return CharacterResult
     */
    public CharacterResult toResult(Character character) {
        if (character == null) {
            return null;
        }

        return CharacterResult.builder()
            .characterId(character.getCharacterId() != null ? character.getCharacterId().value() : null)
            .customerId(character.getCustomerId().value())
            .accountId(character.getAccountConnection().accountId())
            .connectionStatus(character.getAccountConnection().connectionStatus())
            .connectionDate(character.getAccountConnection().connectionDate())
            .terminationReason(character.getAccountConnection().terminationInfo() != null ?
                character.getAccountConnection().terminationInfo().reason() : null)
            .terminatedAt(character.getAccountConnection().terminationInfo() != null ?
                character.getAccountConnection().terminationInfo().terminatedAt() : null)
            .coin(character.getGameStatus().coin())
            .fishCoin(character.getGameStatus().fishCoin())
            .roomCount(character.getGameStatus().roomCount())
            .isActive(character.getGameStatus().isActive())
            .deactivatedAt(character.getCharacterLifecycle().deactivatedAt())
            .createdAt(character.getCharacterLifecycle().createdAt())
            .updatedAt(character.getCharacterLifecycle().updatedAt())
            .build();
    }
}