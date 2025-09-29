package saviing.game.inventory.application.dto.result;

/**
 * 아무것도 반환하지 않는 Result입니다.
 */
public record VoidResult() {

    /**
     * VoidResult의 싱글톤 인스턴스입니다.
     */
    public static final VoidResult INSTANCE = new VoidResult();

    /**
     * VoidResult를 생성합니다.
     *
     * @return VoidResult 인스턴스
     */
    public static VoidResult of() {
        return INSTANCE;
    }
}