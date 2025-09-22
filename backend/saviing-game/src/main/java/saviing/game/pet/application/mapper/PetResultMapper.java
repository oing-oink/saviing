package saviing.game.pet.application.mapper;

import org.springframework.stereotype.Component;
import saviing.game.pet.application.dto.result.PetInfoResult;
import saviing.game.pet.domain.model.aggregate.PetInfo;

/**
 * 펫 도메인 객체를 애플리케이션 결과 DTO로 변환하는 매퍼
 */
@Component
public class PetResultMapper {

    /**
     * PetInfo 도메인 객체를 PetInfoResult DTO로 변환합니다.
     *
     * @param petInfo 펫 정보 도메인 객체
     * @return PetInfoResult DTO
     */
    public PetInfoResult toResult(PetInfo petInfo) {
        if (petInfo == null) {
            return null;
        }

        return PetInfoResult.builder()
            .inventoryItemId(petInfo.getInventoryItemId())
            .level(petInfo.getLevel().value())
            .experience(petInfo.getExperience().value())
            .affection(petInfo.getAffection().value())
            .energy(petInfo.getEnergy().value())
            .petName(petInfo.getPetName())
            .createdAt(petInfo.getCreatedAt())
            .updatedAt(petInfo.getUpdatedAt())
            .build();
    }
}