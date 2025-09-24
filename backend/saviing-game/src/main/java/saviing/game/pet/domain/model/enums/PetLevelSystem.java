package saviing.game.pet.domain.model.enums;

import saviing.game.pet.domain.exception.PetInvalidValueException;

/**
 * 펫 레벨 시스템 설정을 관리하는 enum
 * 레벨별 필요 경험치와 관련 상수들을 중앙에서 관리합니다.
 */
public enum PetLevelSystem {
    INSTANCE;

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

    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 10;

    /**
     * 지정된 레벨에 필요한 총 경험치를 반환합니다.
     *
     * @param level 레벨 (1~10)
     * @return 해당 레벨에 필요한 총 경험치
     * @throws IllegalArgumentException 유효하지 않은 레벨인 경우
     */
    public int getRequiredExpForLevel(int level) {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw PetInvalidValueException.invalidLevel(level);
        }

        if (level == MAX_LEVEL) {
            return 0; // 최대 레벨이면 더 이상 필요한 경험치 없음
        }

        return REQUIRED_EXP_FOR_LEVEL[level]; // 다음 레벨의 필요 경험치
    }

    /**
     * 최소 레벨을 반환합니다.
     */
    public int getMinLevel() {
        return MIN_LEVEL;
    }

    /**
     * 최대 레벨을 반환합니다.
     */
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    /**
     * 유효한 레벨인지 확인합니다.
     *
     * @param level 확인할 레벨
     * @return 유효한 레벨이면 true, 그렇지 않으면 false
     */
    public boolean isValidLevel(int level) {
        return level >= MIN_LEVEL && level <= MAX_LEVEL;
    }
}