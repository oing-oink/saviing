package saviing.game.character.presentation.dto.response;

import lombok.Builder;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.enums.ConnectionStatus;

import java.time.LocalDateTime;

/**
 * 캐릭터 상세 정보 Response
 */
@Builder
public record CharacterResponse(
    Long characterId,
    Long customerId,
    Long accountId,
    ConnectionStatus connectionStatus,
    LocalDateTime connectionDate,
    String terminationReason,
    LocalDateTime terminatedAt,
    Integer coin,
    Integer fishCoin,
    Integer roomCount,
    Boolean isActive,
    LocalDateTime deactivatedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /**
     * Character 도메인 객체로부터 CharacterResponse를 생성합니다.
     * 
     * @param character Character 도메인 객체
     * @return CharacterResponse
     */
    public static CharacterResponse from(Character character) {
        return CharacterResponse.builder()
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