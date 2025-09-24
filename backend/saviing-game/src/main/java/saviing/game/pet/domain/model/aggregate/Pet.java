package saviing.game.pet.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.item.domain.model.vo.ItemId;
import saviing.game.pet.domain.exception.PetInsufficientEnergyException;
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
        int currentLevel = this.level.value();
        if (currentLevel < 1 || currentLevel >= 10) {
            return 0; // 최대 레벨이면 더 이상 필요한 경험치 없음
        }
        return REQUIRED_EXP_FOR_LEVEL[currentLevel]; // 다음 레벨의 필요 경험치
    }
}