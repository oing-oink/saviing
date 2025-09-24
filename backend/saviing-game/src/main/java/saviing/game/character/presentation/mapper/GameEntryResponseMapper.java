package saviing.game.character.presentation.mapper;

import org.springframework.stereotype.Component;
import saviing.game.character.application.dto.result.GameEntryResult;
import saviing.game.character.presentation.dto.response.GameEntryResponse;
import saviing.game.character.presentation.dto.response.GameEntryPetInfo;

/**
 * GameEntryResult를 GameEntryResponse로 변환하는 전용 매퍼
 * 메인 엔트리 게임 정보 API 응답 변환을 담당합니다.
 */
@Component
public class GameEntryResponseMapper {

    /**
     * GameEntryResult를 GameEntryResponse로 변환합니다.
     *
     * @param result GameEntryResult
     * @return GameEntryResponse DTO
     */
    public GameEntryResponse toResponse(GameEntryResult result) {
        if (result == null) {
            return null;
        }

        GameEntryPetInfo petInfo = null;
        if (result.pet() != null) {
            petInfo = GameEntryPetInfo.builder()
                .petId(result.pet().petId())
                .itemId(result.pet().itemId())
                .name(result.pet().petName())
                .level(result.pet().level())
                .exp(result.pet().experience())
                .requiredExp(result.pet().requiredExp())
                .affection(result.pet().affection())
                .maxAffection(calculateMaxAffection())
                .energy(result.pet().energy())
                .maxEnergy(calculateMaxEnergy())
                .build();
        }

        return GameEntryResponse.builder()
            .characterId(result.characterId())
            .roomId(result.roomId())
            .pet(petInfo)
            .build();
    }


    /**
     * 최대 애정도를 계산합니다.
     * TODO: 펫별 최대 애정도 설정 반영 필요
     */
    private Integer calculateMaxAffection() {
        return 100;
    }

    /**
     * 최대 에너지를 계산합니다.
     * TODO: 펫별 최대 에너지 설정 반영 필요
     */
    private Integer calculateMaxEnergy() {
        return 100;
    }
}