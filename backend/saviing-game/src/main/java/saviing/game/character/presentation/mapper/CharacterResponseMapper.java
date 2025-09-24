package saviing.game.character.presentation.mapper;

import org.springframework.stereotype.Component;
import saviing.game.character.application.dto.result.CharacterCreatedResult;
import saviing.game.character.application.dto.result.CharacterListResult;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.character.presentation.dto.response.CharacterResponse;

import java.util.List;

/**
 * Application Result를 CharacterResponse DTO로 변환하는 Mapper
 * Presentation 계층에서 Application Result를 API 응답용 DTO로 변환합니다.
 */
@Component
public class CharacterResponseMapper {

    /**
     * CharacterResult를 CharacterResponse로 변환합니다.
     *
     * @param result CharacterResult
     * @return CharacterResponse DTO
     */
    public CharacterResponse toResponse(CharacterResult result) {
        if (result == null) {
            return null;
        }

        return CharacterResponse.builder()
            .characterId(result.characterId())
            .customerId(result.customerId())
            .accountId(result.accountId())
            .connectionStatus(result.connectionStatus())
            .connectionDate(result.connectionDate())
            .terminationReason(result.terminationReason())
            .terminatedAt(result.terminatedAt())
            .coin(result.coin())
            .fishCoin(result.fishCoin())
            .roomCount(result.roomCount())
            .isActive(result.isActive())
            .deactivatedAt(result.deactivatedAt())
            .createdAt(result.createdAt())
            .updatedAt(result.updatedAt())
            .build();
    }

    /**
     * CharacterCreatedResult를 CharacterResponse로 변환합니다.
     *
     * @param result CharacterCreatedResult
     * @return CharacterResponse DTO
     */
    public CharacterResponse toResponse(CharacterCreatedResult result) {
        if (result == null) {
            return null;
        }

        return CharacterResponse.builder()
            .characterId(result.characterId())
            .customerId(result.customerId())
            .roomId(result.roomId())
            .createdAt(result.createdAt())
            .build();
    }

    /**
     * CharacterListResult를 CharacterResponse 목록으로 변환합니다.
     *
     * @param result CharacterListResult
     * @return CharacterResponse DTO 목록
     */
    public List<CharacterResponse> toResponse(CharacterListResult result) {
        if (result == null) {
            return List.of();
        }
        if (result.characters() == null) {
            return List.of();
        }

        return result.characters().stream()
            .map(this::toResponse)
            .toList();
    }
}