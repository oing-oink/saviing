package saviing.game.item.domain.exception;

/**
 * 잘못된 아이템 정의로 인해 발생하는 예외
 */
public class InvalidItemDefinitionException extends ItemException {

    public InvalidItemDefinitionException() {
        super(ItemErrorCode.ITEM_INVALID_DEFINITION);
    }

    public InvalidItemDefinitionException(String message) {
        super(ItemErrorCode.ITEM_INVALID_DEFINITION, message);
    }

    /**
     * 아이템 이름이 잘못된 경우 예외를 생성합니다.
     *
     * @param name 잘못된 아이템 이름
     * @return InvalidItemDefinitionException 인스턴스
     */
    public static InvalidItemDefinitionException invalidName(String name) {
        return new InvalidItemDefinitionException(
            String.format("잘못된 아이템 이름입니다: %s", name)
        );
    }

    /**
     * 아이템 설명이 잘못된 경우 예외를 생성합니다.
     *
     * @param description 잘못된 아이템 설명
     * @return InvalidItemDefinitionException 인스턴스
     */
    public static InvalidItemDefinitionException invalidDescription(String description) {
        return new InvalidItemDefinitionException(
            String.format("잘못된 아이템 설명입니다: %s", description)
        );
    }

    /**
     * 아이템 가격이 잘못된 경우 예외를 생성합니다.
     *
     * @param reason 가격이 잘못된 이유
     * @return InvalidItemDefinitionException 인스턴스
     */
    public static InvalidItemDefinitionException invalidPrice(String reason) {
        return new InvalidItemDefinitionException(
            String.format("잘못된 아이템 가격입니다: %s", reason)
        );
    }

    /**
     * 아이템 크기가 잘못된 경우 예외를 생성합니다.
     *
     * @param xLength X 길이
     * @param yLength Y 길이
     * @return InvalidItemDefinitionException 인스턴스
     */
    public static InvalidItemDefinitionException invalidSize(int xLength, int yLength) {
        return new InvalidItemDefinitionException(
            String.format("잘못된 아이템 크기입니다: %dx%d", xLength, yLength)
        );
    }

    /**
     * 아이템 이미지 URL이 잘못된 경우 예외를 생성합니다.
     *
     * @param imageUrl 잘못된 이미지 URL
     * @return InvalidItemDefinitionException 인스턴스
     */
    public static InvalidItemDefinitionException invalidImageUrl(String imageUrl) {
        return new InvalidItemDefinitionException(
            String.format("잘못된 이미지 URL입니다: %s", imageUrl)
        );
    }

    /**
     * 아이템 카테고리와 타입이 일치하지 않는 경우 예외를 생성합니다.
     *
     * @param category 아이템 카테고리
     * @param type 아이템 타입
     * @return InvalidItemDefinitionException 인스턴스
     */
    public static InvalidItemDefinitionException categoryTypeMismatch(String category, String type) {
        return new InvalidItemDefinitionException(
            String.format("아이템 카테고리(%s)와 타입(%s)이 일치하지 않습니다", category, type)
        );
    }

    /**
     * 데코레이션 아이템에 크기 정보가 없는 경우 예외를 생성합니다.
     *
     * @return InvalidItemDefinitionException 인스턴스
     */
    public static InvalidItemDefinitionException decorationSizeRequired() {
        return new InvalidItemDefinitionException(
            "데코레이션 아이템은 x,y 좌표가 필수입니다"
        );
    }
}