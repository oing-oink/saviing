package saviing.game.pet.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.pet.application.dto.command.ApplyAffectionDecayCommand;
import saviing.game.pet.application.dto.command.CreatePetCommand;
import saviing.game.pet.application.dto.command.InteractWithPetCommand;
import saviing.game.pet.application.dto.result.PetInfoResult;
import saviing.game.pet.application.mapper.PetResultMapper;
import saviing.game.pet.domain.exception.PetAlreadyExistsException;
import saviing.game.pet.domain.exception.PetNotFoundException;
import saviing.game.pet.domain.model.aggregate.PetInfo;
import saviing.game.pet.domain.model.vo.Affection;
import saviing.game.pet.domain.model.vo.Energy;
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
     * @param command 펫 상호작용 명령
     * @return 상호작용 후 펫 정보
     */
    public PetInfoResult interactWithPet(InteractWithPetCommand command) {
        log.info("펫 상호작용 시작: inventoryItemId={}, energyCost={}, affectionGain={}",
            command.inventoryItemId().value(), command.energyCost(), command.affectionGain());

        PetInfo petInfo = petInfoRepository.findById(command.inventoryItemId())
            .orElseThrow(() -> new PetNotFoundException(command.inventoryItemId().value()));

        // 상호작용 실행
        petInfo.interact(Energy.of(command.energyCost()), Affection.of(command.affectionGain()));

        // 저장
        PetInfo savedPetInfo = petInfoRepository.save(petInfo);

        log.info("펫 상호작용 완료: inventoryItemId={}, energy={}, affection={}",
            command.inventoryItemId().value(), savedPetInfo.getEnergy().value(), savedPetInfo.getAffection().value());

        return petResultMapper.toResult(savedPetInfo);
    }

    /**
     * 시간 경과에 따른 애정도 감소를 적용합니다.
     *
     * @param command 애정도 감소 적용 명령
     * @return 애정도 감소 적용 후 펫 정보
     */
    public PetInfoResult applyAffectionDecay(ApplyAffectionDecayCommand command) {
        log.debug("애정도 감소 적용 시작: inventoryItemId={}", command.inventoryItemId().value());

        PetInfo petInfo = petInfoRepository.findById(command.inventoryItemId())
            .orElseThrow(() -> new PetNotFoundException(command.inventoryItemId().value()));

        // 애정도 감소 적용
        petInfo.applyAffectionDecay(command.lastAccessTime());

        // 저장
        PetInfo savedPetInfo = petInfoRepository.save(petInfo);

        log.debug("애정도 감소 적용 완료: inventoryItemId={}, affection={}",
            command.inventoryItemId().value(), savedPetInfo.getAffection().value());

        return petResultMapper.toResult(savedPetInfo);
    }
}