package saviing.game.pet.presentation.mapper;

import org.springframework.stereotype.Component;
import saviing.game.pet.application.dto.result.PetResult;
import saviing.game.pet.presentation.dto.response.PetInfoResponse;

/**
 * PetResult를 PetInfoResponse로 변환하는 매퍼
 */
@Component
public class PetResponseMapper {

    private static final int MAX_AFFECTION = 100;
    private static final int MAX_ENERGY = 100;

    // 레벨별 필요 경험치 배열 (인덱스 0 = 레벨 1)
    private static final int[] REQUIRED_EXP_FOR_LEVEL = {
        0,      // 레벨 1: 0
        100,    // 레벨 2: 100
        300,    // 레벨 3: 300
        600,    // 레벨 4: 600
        1000,   // 레벨 5: 1000
        1500,   // 레벨 6: 1500
        2100,   // 레벨 7: 2100
        2800,   // 레벨 8: 2800
        3600,   // 레벨 9: 3600
        4500    // 레벨 10: 4500
    };

    /**
     * PetResult를 PetInfoResponse로 변환합니다.
     *
     * @param result PetResult
     * @return PetInfoResponse
     */
    public PetInfoResponse toResponse(PetResult result) {
        if (result == null) {
            return null;
        }

        return PetInfoResponse.builder()
            .petId(result.petId())
            .itemId(result.itemId())
            .name(result.petName())
            .level(result.level())
            .exp(result.experience())
            .requiredExp(calculateRequiredExp(result.level()))
            .affection(result.affection())
            .maxAffection(MAX_AFFECTION)
            .energy(result.energy())
            .maxEnergy(MAX_ENERGY)
            .build();
    }

    /**
     * 레벨에 따른 다음 레벨 달성에 필요한 경험치를 계산합니다.
     *
     * @param level 현재 레벨
     * @return 다음 레벨 달성에 필요한 총 경험치
     */
    private int calculateRequiredExp(Integer level) {
        if (level == null || level < 1 || level >= 10) {
            return 0; // 최대 레벨이면 더 이상 필요한 경험치 없음
        }
        return REQUIRED_EXP_FOR_LEVEL[level]; // 다음 레벨의 필요 경험치
    }
}