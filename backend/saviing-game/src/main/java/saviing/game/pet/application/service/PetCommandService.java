package saviing.game.pet.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saviing.game.inventory.domain.model.aggregate.ConsumptionInventory;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.inventory.domain.repository.InventoryRepository;
import saviing.game.pet.application.dto.command.ApplyAffectionDecayCommand;
import saviing.game.pet.application.dto.command.CreatePetCommand;
import saviing.game.pet.application.dto.command.InteractWithPetCommand;
import saviing.game.pet.application.dto.result.PetResult;
import saviing.game.pet.application.mapper.PetResultMapper;
import saviing.game.pet.domain.exception.PetAlreadyExistsException;
import saviing.game.pet.domain.exception.PetNotFoundException;
import saviing.game.pet.domain.model.aggregate.Pet;
import saviing.game.pet.domain.model.vo.Affection;
import saviing.game.pet.domain.model.vo.Energy;
import saviing.game.pet.domain.model.vo.Experience;
import saviing.game.pet.domain.repository.PetRepository;

/**
 * 펫 명령 처리 서비스
 * 펫의 생성, 수정 등의 작업을 처리합니다.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PetCommandService {

    private final PetRepository petRepository;
    private final InventoryRepository inventoryRepository;
    private final PetResultMapper petResultMapper;

    /**
     * 새로운 펫을 생성합니다.
     * PET 아이템 구매 시 호출됩니다.
     *
     * @param command 펫 생성 명령
     * @return 생성된 펫 결과
     */
    public PetResult createPet(CreatePetCommand command) {
        log.info("펫 생성 시작: inventoryItemId={}", command.inventoryItemId().value());

        // 중복 생성 방지
        if (petRepository.existsById(command.inventoryItemId())) {
            log.warn("이미 존재하는 펫입니다: inventoryItemId={}", command.inventoryItemId().value());
            throw new PetAlreadyExistsException(command.inventoryItemId());
        }

        // 펫 생성
        Pet pet = Pet.create(command.inventoryItemId(), command.itemName());

        // 저장
        Pet savedPet = petRepository.save(pet);

        log.info("펫 생성 완료: inventoryItemId={}, level={}, affection={}",
            savedPet.getInventoryItemId().value(),
            savedPet.getLevel().value(),
            savedPet.getAffection().value());

        return petResultMapper.toResult(savedPet);
    }

    /**
     * 펫과 상호작용합니다 (놀아주기, 먹이주기 등)
     * 소모품을 사용하고 펫의 상태를 변경합니다.
     *
     * @param command 펫 상호작용 명령
     * @return 상호작용 후 펫 정보
     */
    public PetResult interactWithPet(InteractWithPetCommand command) {
        log.info("펫 상호작용 시작: inventoryItemId={}, interactionType={}",
            command.inventoryItemId().value(), command.interactionType());

        // 펫 조회
        Pet pet = petRepository.findById(command.inventoryItemId())
            .orElseThrow(() -> new PetNotFoundException(command.inventoryItemId().value()));

        // 필요한 소모품 조회 및 검증
        var requiredConsumption = command.interactionType().getRequiredConsumption();

        // TODO: 소모품 아이템 ID를 어떻게 결정할지 확인 필요 - 일단 임시로 처리
        // 실제 구현 시에는 캐릭터가 가진 해당 카테고리의 소모품 중 하나를 선택해야 함

        // 상호작용 실행 (임시로 Experience는 고정값 사용)
        Experience requiredExp = Experience.of(100); // 임시값 - 실제로는 레벨 테이블에서 가져와야 함
        pet.interactWithType(command.interactionType(), requiredExp);

        // 저장
        Pet savedPet = petRepository.save(pet);

        log.info("펫 상호작용 완료: inventoryItemId={}, interactionType={}, energy={}, affection={}, exp={}",
            command.inventoryItemId().value(), command.interactionType(),
            savedPet.getEnergy().value(), savedPet.getAffection().value(), savedPet.getExperience().value());

        return petResultMapper.toResult(savedPet);
    }

    /**
     * 시간 경과에 따른 애정도 감소를 적용합니다.
     *
     * @param command 애정도 감소 적용 명령
     * @return 애정도 감소 적용 후 펫 정보
     */
    public PetResult applyAffectionDecay(ApplyAffectionDecayCommand command) {
        log.debug("애정도 감소 적용 시작: inventoryItemId={}", command.inventoryItemId().value());

        Pet pet = petRepository.findById(command.inventoryItemId())
            .orElseThrow(() -> new PetNotFoundException(command.inventoryItemId().value()));

        // 애정도 감소 적용
        pet.applyAffectionDecay(command.lastAccessTime());

        // 저장
        Pet savedPet = petRepository.save(pet);

        log.debug("애정도 감소 적용 완료: inventoryItemId={}, affection={}",
            command.inventoryItemId().value(), savedPet.getAffection().value());

        return petResultMapper.toResult(savedPet);
    }
}