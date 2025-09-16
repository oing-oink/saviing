package saviing.game.item.application.dto.result;

/**
 * 반환값이 없는 작업의 결과를 나타내는 DTO
 */
public record VoidResult() {

    /**
     * 기본 VoidResult 인스턴스를 생성합니다.
     *
     * @return VoidResult 인스턴스
     */
    public static VoidResult of() {
        return new VoidResult();
    }
}