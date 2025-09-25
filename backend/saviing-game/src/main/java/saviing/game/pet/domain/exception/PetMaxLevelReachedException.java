package saviing.game.pet.domain.exception;

/**
 * 펫이 이미 최대 레벨에 도달한 상태에서 레벨업을 시도할 때 발생하는 예외
 */
public class PetMaxLevelReachedException extends PetException {

    public PetMaxLevelReachedException(int currentLevel, int maxLevel) {
        super(PetErrorCode.PET_MAX_LEVEL_REACHED,
              String.format("이미 최대 레벨에 도달했습니다. 현재 레벨: %d, 최대 레벨: %d", currentLevel, maxLevel));
    }
}