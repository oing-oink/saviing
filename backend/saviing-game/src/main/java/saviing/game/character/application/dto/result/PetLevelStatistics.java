package saviing.game.character.application.dto.result;

/**
 * 펫 레벨 통계 Value Object
 * 캐릭터가 보유한 펫들의 레벨 합계 정보를 담는 불변 객체입니다.
 */
public record PetLevelStatistics(
    Integer totalLevelSum
) {

    /**
     * 펫 레벨 합계를 기반으로 PetLevelStatistics를 생성합니다.
     *
     * @param totalLevelSum 펫들의 총 레벨 합계
     * @return PetLevelStatistics 인스턴스
     */
    public static PetLevelStatistics of(Integer totalLevelSum) {
        return new PetLevelStatistics(totalLevelSum != null ? totalLevelSum : 0);
    }
}