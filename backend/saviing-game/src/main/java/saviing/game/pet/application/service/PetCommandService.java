package saviing.game.pet.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.pet.application.dto.command.CreatePetCommand;
import saviing.game.pet.application.dto.result.PetInfoResult;
import saviing.game.pet.application.mapper.PetResultMapper;
import saviing.game.pet.domain.exception.PetAlreadyExistsException;
import saviing.game.pet.domain.exception.PetNotFoundException;
import saviing.game.pet.domain.model.aggregate.PetInfo;
import saviing.game.pet.domain.repository.PetInfoRepository;

/**
 * 펫 명령 처리 서비스
 * 펫의 생성, 수정 등의 작업을 처리합니다.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PetCommandService {

    private final PetInfoRepository petInfoRepository;
    private final PetResultMapper petResultMapper;

    /**
     * 새로운 펫 정보를 생성합니다.
     * PET 아이템 구매 시 호출됩니다.
     *
     * @param command 펫 생성 명령
     * @return 생성된 펫 정보 결과
     */
    public PetInfoResult createPet(CreatePetCommand command) {
        log.info("펫 정보 생성 시작: inventoryItemId={}", command.inventoryItemId().value());

        // 중복 생성 방지
        if (petInfoRepository.existsById(command.inventoryItemId())) {
            log.warn("이미 존재하는 펫 정보입니다: inventoryItemId={}", command.inventoryItemId().value());
            throw new PetAlreadyExistsException(command.inventoryItemId());
        }

        // 펫 정보 생성
        PetInfo petInfo = PetInfo.create(command.inventoryItemId());

        // 저장
        PetInfo savedPetInfo = petInfoRepository.save(petInfo);

        log.info("펫 정보 생성 완료: inventoryItemId={}, level={}, affection={}",
            savedPetInfo.getInventoryItemId().value(),
            savedPetInfo.getLevel().value(),
            savedPetInfo.getAffection().value());

        return petResultMapper.toResult(savedPetInfo);
    }

    /**
     * 펫과 상호작용합니다 (놀아주기, 먹이주기 등)
     *
     * @param inventoryItemId 펫의 인벤토리 아이템 ID
     * @param energyCost 소모할 포만감
     * @param affectionGain 증가할 애정도
     * @return 상호작용 후 펫 정보
     */
    public PetInfoResult interactWithPet(Long inventoryItemId, int energyCost, int affectionGain) {
        log.info("펫 상호작용 시작: inventoryItemId={}, energyCost={}, affectionGain={}",
            inventoryItemId, energyCost, affectionGain);

        PetInfo petInfo = petInfoRepository.findById(InventoryItemId.of(inventoryItemId))
            .orElseThrow(() -> new PetNotFoundException(inventoryItemId));

        // 상호작용 실행
        petInfo.interact(energyCost, affectionGain);

        // 저장
        PetInfo savedPetInfo = petInfoRepository.save(petInfo);

        log.info("펫 상호작용 완료: inventoryItemId={}, energy={}, affection={}",
            inventoryItemId, savedPetInfo.getEnergy().value(), savedPetInfo.getAffection().value());

        return petResultMapper.toResult(savedPetInfo);
    }

    /**
     * 시간 경과에 따른 애정도 감소를 적용합니다.
     *
     * @param inventoryItemId 펫의 인벤토리 아이템 ID
     * @param lastAccessTime 마지막 접속 시간
     * @return 애정도 감소 적용 후 펫 정보
     */
    public PetInfoResult applyAffectionDecay(Long inventoryItemId, java.time.LocalDateTime lastAccessTime) {
        log.debug("애정도 감소 적용 시작: inventoryItemId={}", inventoryItemId);

        PetInfo petInfo = petInfoRepository.findById(InventoryItemId.of(inventoryItemId))
            .orElseThrow(() -> new PetNotFoundException(inventoryItemId));

        // 애정도 감소 적용
        petInfo.applyAffectionDecay(lastAccessTime);

        // 저장
        PetInfo savedPetInfo = petInfoRepository.save(petInfo);

        log.debug("애정도 감소 적용 완료: inventoryItemId={}, affection={}",
            inventoryItemId, savedPetInfo.getAffection().value());

        return petResultMapper.toResult(savedPetInfo);
    }
}