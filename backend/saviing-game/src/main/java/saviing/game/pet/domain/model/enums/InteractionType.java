package saviing.game.pet.domain.model.enums;

import saviing.game.item.domain.model.enums.Consumption;

/**
 * 펫 상호작용 타입 열거형
 * 펫과의 상호작용 종류를 정의합니다.
 */
public enum InteractionType {
    /**
     * 먹이주기
     * FOOD 타입 소모품을 사용합니다.
     * 에너지를 회복시키고 애정도를 증가시킵니다.
     */
    FOOD(Consumption.FOOD, 20, 5, 0),

    /**
     * 놀아주기
     * TOY 타입 소모품을 사용합니다.
     * 에너지를 소모하고 애정도와 경험치를 증가시킵니다.
     */
    TOY(Consumption.TOY, -15, 10, 25);

    private final Consumption requiredConsumption;
    private final int energyChange;
    private final int affectionGain;
    private final int experienceGain;

    InteractionType(Consumption requiredConsumption, int energyChange, int affectionGain, int experienceGain) {
        this.requiredConsumption = requiredConsumption;
        this.energyChange = energyChange;
        this.affectionGain = affectionGain;
        this.experienceGain = experienceGain;
    }

    /**
     * 상호작용에 필요한 소모품 카테고리를 반환합니다.
     */
    public Consumption getRequiredConsumption() {
        return requiredConsumption;
    }

    /**
     * 상호작용 시 변화하는 에너지 양을 반환합니다.
     * 양수: 에너지 회복, 음수: 에너지 소모
     */
    public int getEnergyChange() {
        return energyChange;
    }

    /**
     * 상호작용 시 증가하는 애정도를 반환합니다.
     */
    public int getAffectionGain() {
        return affectionGain;
    }

    /**
     * 상호작용 시 획득하는 경험치를 반환합니다.
     */
    public int getExperienceGain() {
        return experienceGain;
    }

    /**
     * 에너지를 소모하는 상호작용인지 확인합니다.
     */
    public boolean consumesEnergy() {
        return energyChange < 0;
    }

    /**
     * 에너지를 회복하는 상호작용인지 확인합니다.
     */
    public boolean recoversEnergy() {
        return energyChange > 0;
    }
}