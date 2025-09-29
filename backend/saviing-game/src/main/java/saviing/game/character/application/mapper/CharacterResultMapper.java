package saviing.game.character.application.mapper;

import org.springframework.stereotype.Component;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.character.application.dto.result.CharacterStatisticsResult;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CharacterId;

import java.util.Map;

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

    /**
     * 캐릭터 통계 정보를 CharacterStatisticsResult로 변환합니다.
     *
     * @param characterId 캐릭터 ID
     * @param topPetLevelSum 상위 펫들의 레벨 합계
     * @param inventoryRarityStatistics ItemType별로 그룹화된 인벤토리 희귀도 통계
     * @return CharacterStatisticsResult (이자율은 null, 서비스 레이어에서 계산 후 설정)
     */
    public CharacterStatisticsResult toStatisticsResult(
        CharacterId characterId,
        Integer topPetLevelSum,
        Map<String, Map<String, Integer>> inventoryRarityStatistics
    ) {
        return CharacterStatisticsResult.builder()
            .characterId(characterId != null ? characterId.value() : null)
            .topPetLevelSum(topPetLevelSum != null ? topPetLevelSum : 0)
            .inventoryRarityStatistics(
                inventoryRarityStatistics != null ? inventoryRarityStatistics : Map.of()
            )
            .calculatedInterestRate(null) // 초기값 null, 서비스에서 계산 후 설정
            .build();
    }
}