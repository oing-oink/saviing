package saviing.game.character.domain.model.vo;

/**
 * 캐릭터 식별자 Value Object
 */
public record CharacterId(
    Long value
) {
    public CharacterId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("캐릭터 ID는 양수여야 합니다");
        }
    }

    /**
     * Long 값으로 CharacterId를 생성합니다.
     * 
     * @param value 캐릭터 ID 값
     * @return CharacterId 인스턴스
     */
    public static CharacterId of(Long value) {
        return new CharacterId(value);
    }
}