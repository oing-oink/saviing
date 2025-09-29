package saviing.game.pet.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.item.domain.model.vo.ItemId;
import saviing.game.pet.domain.exception.PetInsufficientEnergyException;
import saviing.game.pet.domain.model.enums.InteractionType;
import saviing.game.pet.domain.model.enums.PetLevelSystem;
import saviing.game.pet.domain.model.vo.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 펫 Aggregate Root
 * 펫의 상태 정보(레벨, 경험치, 애정도, 포만감)를 관리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet {

    private InventoryItemId inventoryItemId;  // PK: pet의 inventory_item_id
    private PetLevel level;
    private Experience experience;
    private Affection affection;
    private Energy energy;
    private PetName petName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Pet(
        InventoryItemId inventoryItemId,
        PetLevel level,
        Experience experience,
        Affection affection,
        Energy energy,
        PetName petName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.inventoryItemId = inventoryItemId;
        this.level = level;
        this.experience = experience;
        this.affection = affection;
        this.energy = energy;
        this.petName = petName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        validateInvariants();
    }

    /**
     * 새로운 펫을 생성합니다.
     * ItemPurchasedEvent 핸들러에서 PET 아이템 구매 시 호출됩니다.
     */
    public static Pet create(InventoryItemId inventoryItemId, String itemName) {
        LocalDateTime now = LocalDateTime.now();

        return Pet.builder()
            .inventoryItemId(inventoryItemId)
            .level(PetLevel.initial())
            .experience(Experience.initial())
            .affection(Affection.initial())
            .energy(Energy.initial())
            .petName(PetName.fromItemName(itemName))  // 아이템 이름을 기본 펫 이름으로 설정
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    /**
     * 경험치를 획득하고 레벨업을 확인합니다.
     */
    public void gainExperience(Experience amount, Experience requiredExpForNextLevel) {
        // Aggregate 레벨에서 null 안전성 보장 (값 검증은 VO에서)
        Experience newExperience = this.experience.add(amount);

        // 레벨업 가능 여부 확인
        if (level.canLevelUp(newExperience, requiredExpForNextLevel)) {
            this.level = level.levelUp();
            this.experience = newExperience;
            updateTimestamp();
        } else {
            this.experience = newExperience;
            updateTimestamp();
        }
    }

    /**
     * 애정도를 증가시킵니다 (상호작용, 먹이주기 등)
     */
    public void increaseAffection(Affection amount) {
        // Aggregate 레벨에서 null 안전성 보장 (값 검증은 VO에서)
        this.affection = this.affection.increase(amount);
        updateTimestamp();
    }

    /**
     * 애정도를 감소시킵니다 (시간 경과 등)
     */
    public void decreaseAffection(Affection amount) {
        // Aggregate 레벨에서 null 안전성 보장 (값 검증은 VO에서)
        this.affection = this.affection.decrease(amount);
        updateTimestamp();
    }

    /**
     * 시간 경과에 따른 애정도 자동 감소를 적용합니다.
     */
    public void applyAffectionDecay(LocalDateTime lastAccessTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        Affection oldAffection = this.affection;
        this.affection = this.affection.applyTimeDecay(lastAccessTime, currentTime);

        // 애정도가 실제로 변경된 경우에만 타임스탬프 업데이트
        if (!oldAffection.equals(this.affection)) {
            updateTimestamp();
        }
    }

    /**
     * 포만감을 소모합니다 (활동, 놀기 등)
     */
    public void consumeEnergy(Energy amount) {
        // Aggregate 레벨에서 null 안전성 보장 (값 검증은 VO에서)
        this.energy = this.energy.consume(amount);
        updateTimestamp();
    }

    /**
     * 포만감을 회복합니다 (시간 경과, 휴식 등)
     */
    public void recoverEnergy(Energy amount) {
        // Aggregate 레벨에서 null 안전성 보장 (값 검증은 VO에서)
        this.energy = this.energy.recover(amount);
        updateTimestamp();
    }

    /**
     * 특정 활동에 필요한 포만감이 있는지 확인합니다.
     */
    public boolean hasEnoughEnergyFor(Energy requiredEnergy) {
        return this.energy.hasEnoughEnergy(requiredEnergy);
    }

    /**
     * 펫과 상호작용합니다 (놀아주기, 먹이주기 등)
     * 포만감 소모와 애정도 증가를 동시에 처리합니다.
     */
    public void interact(Energy energyCost, Affection affectionGain) {
        if (!hasEnoughEnergyFor(energyCost)) {
            throw new PetInsufficientEnergyException(this.energy.value(), energyCost.value());
        }

        consumeEnergy(energyCost);
        increaseAffection(affectionGain);
    }

    /**
     * 특정 타입의 상호작용을 수행합니다.
     * 상호작용 타입에 따라 에너지, 애정도, 경험치 변화를 처리합니다.
     */
    public void interactWithType(InteractionType interactionType, Experience requiredExpForNextLevel) {
        // 에너지 소모 상호작용의 경우 에너지 확인
        if (interactionType.consumesEnergy()) {
            Energy requiredEnergy = Energy.of(Math.abs(interactionType.getEnergyChange()));
            if (!hasEnoughEnergyFor(requiredEnergy)) {
                throw new PetInsufficientEnergyException(this.energy.value(), requiredEnergy.value());
            }
            consumeEnergy(requiredEnergy);
        }

        // 에너지 회복 상호작용의 경우
        if (interactionType.recoversEnergy()) {
            Energy recoveryAmount = Energy.of(interactionType.getEnergyChange());
            recoverEnergy(recoveryAmount);
        }

        // 애정도 증가
        Affection affectionGain = Affection.of(interactionType.getAffectionGain());
        increaseAffection(affectionGain);

        // 경험치 증가 (0이 아닌 경우만)
        if (interactionType.getExperienceGain() > 0) {
            Experience expGain = Experience.of(interactionType.getExperienceGain());
            gainExperience(expGain, requiredExpForNextLevel);
        }
    }

    /**
     * 먹이주기 상호작용을 수행합니다.
     * 에너지를 회복하고 애정도를 증가시킵니다.
     */
    public void feed(Experience requiredExpForNextLevel) {
        interactWithType(InteractionType.FOOD, requiredExpForNextLevel);
    }

    /**
     * 놀아주기 상호작용을 수행합니다.
     * 에너지를 소모하고 애정도와 경험치를 증가시킵니다.
     */
    public void play(Experience requiredExpForNextLevel) {
        interactWithType(InteractionType.TOY, requiredExpForNextLevel);
    }

    /**
     * 펫의 이름을 변경합니다.
     */
    public void changePetName(PetName newName) {
        this.petName = newName;
        updateTimestamp();
    }

    private void validateInvariants() {
        Objects.requireNonNull(inventoryItemId, "인벤토리 아이템 ID는 null일 수 없습니다");
        Objects.requireNonNull(level, "펫 레벨은 null일 수 없습니다");
        Objects.requireNonNull(experience, "펫 경험치는 null일 수 없습니다");
        Objects.requireNonNull(affection, "펫 애정도는 null일 수 없습니다");
        Objects.requireNonNull(energy, "펫 포만감은 null일 수 없습니다");
        Objects.requireNonNull(petName, "펫 이름은 null일 수 없습니다");
        Objects.requireNonNull(createdAt, "생성 시간은 null일 수 없습니다");
        Objects.requireNonNull(updatedAt, "수정 시간은 null일 수 없습니다");
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 펫 ID를 반환합니다 (편의 메서드)
     */
    public PetId getPetId() {
        return PetId.of(inventoryItemId.value());
    }

    /**
     * 다음 레벨 달성에 필요한 경험치를 계산합니다.
     *
     * @return 다음 레벨 달성에 필요한 총 경험치
     */
    public int calculateRequiredExpForNextLevel() {
        return PetLevelSystem.INSTANCE.getRequiredExpForLevel(this.level.value());
    }
}