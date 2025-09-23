package saviing.game.pet.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.inventory.domain.repository.InventoryRepository;
import saviing.game.item.domain.model.vo.ItemId;
import saviing.game.pet.application.dto.query.GetPetInfoQuery;
import saviing.game.pet.application.dto.result.PetResult;
import saviing.game.pet.application.mapper.PetResultMapper;
import saviing.game.pet.domain.exception.PetForbiddenException;
import saviing.game.pet.domain.exception.PetNotFoundException;
import saviing.game.pet.domain.model.aggregate.Pet;
import saviing.game.pet.domain.repository.PetRepository;

/**
 * 펫 Query 처리 서비스
 * 조회를 담당하는 Query 처리를 담당합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PetQueryService {

    private final PetRepository petRepository;
    private final InventoryRepository inventoryRepository;
    private final PetResultMapper petResultMapper;

    /**
     * 펫 정보를 조회합니다. 소유권 확인이 포함됩니다.
     *
     * @param query 펫 정보 조회 Query
     * @return 펫 조회 결과
     */
    public PetResult getPetInfo(GetPetInfoQuery query) {
        log.info("펫 정보 조회 시작: inventoryItemId={}, characterId={}",
            query.inventoryItemId().value(), query.characterId().value());

        // 1. 펫 존재 여부 확인
        Pet pet = petRepository.findById(query.inventoryItemId())
            .orElseThrow(() -> new PetNotFoundException(query.inventoryItemId()));

        // 2. 결과 매핑 및 소유권 확인
        PetResult result = petResultMapper.toResult(pet);

        // 3. 소유권 확인 (매퍼에서 ItemId를 조회했으므로 사용 가능)
        if (!inventoryRepository.existsByCharacterIdAndItemId(query.characterId(), ItemId.of(result.itemId()))) {
            log.warn("펫 접근 권한 없음: inventoryItemId={}, characterId={}",
                query.inventoryItemId().value(), query.characterId().value());
            throw new PetForbiddenException(pet.getPetId(), query.characterId());
        }

        log.info("펫 정보 조회 완료: petId={}, petName={}",
            result.petId(), result.petName());

        return result;
    }
}