package saviing.game.character.application.dto.result;

/**
 * 반환값이 없는 작업의 결과 Result입니다.
 */
public record VoidResult() {
    /**
     * 성공 결과를 나타내는 VoidResult를 생성합니다.
     *
     * @return VoidResult 인스턴스
     */
    public static VoidResult success() {
        return new VoidResult();
    }
}