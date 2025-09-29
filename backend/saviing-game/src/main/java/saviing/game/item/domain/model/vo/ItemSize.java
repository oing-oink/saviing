package saviing.game.item.domain.model.vo;

/**
 * 아이템 크기 Value Object
 * 아이템의 X, Y 길이를 나타냅니다.
 * 데코레이션 아이템은 x,y가 필수이고, 다른 타입은 null 허용됩니다.
 */
public record ItemSize(
    Integer xLength,
    Integer yLength
) {
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 24;

    public ItemSize {
        if (xLength != null && (xLength < MIN_SIZE || xLength > MAX_SIZE)) {
            throw new IllegalArgumentException(
                String.format("X 길이는 %d 이상 %d 이하여야 합니다", MIN_SIZE, MAX_SIZE)
            );
        }
        if (yLength != null && (yLength < MIN_SIZE || yLength > MAX_SIZE)) {
            throw new IllegalArgumentException(
                String.format("Y 길이는 %d 이상 %d 이하여야 합니다", MIN_SIZE, MAX_SIZE)
            );
        }
    }

    /**
     * X, Y 길이로 ItemSize를 생성합니다.
     *
     * @param xLength X 길이 (null 허용)
     * @param yLength Y 길이 (null 허용)
     * @return ItemSize 인스턴스
     */
    public static ItemSize of(Integer xLength, Integer yLength) {
        return new ItemSize(xLength, yLength);
    }

    /**
     * 데코레이션 아이템용 필수 크기를 생성합니다.
     *
     * @param xLength X 길이 (필수)
     * @param yLength Y 길이 (필수)
     * @return ItemSize 인스턴스
     */
    public static ItemSize required(int xLength, int yLength) {
        return new ItemSize(xLength, yLength);
    }

    /**
     * PET, ACCESSORY용 선택적 크기를 생성합니다 (null 값).
     *
     * @return null 값을 가진 ItemSize 인스턴스
     */
    public static ItemSize optional() {
        return new ItemSize(null, null);
    }

    /**
     * 1x1 크기의 기본 ItemSize를 생성합니다.
     *
     * @return 1x1 크기의 ItemSize 인스턴스
     */
    public static ItemSize defaultSize() {
        return new ItemSize(1, 1);
    }

    /**
     * 아이템이 차지하는 총 면적을 계산합니다.
     *
     * @return 총 면적 (xLength * yLength), null인 경우 0
     */
    public int getArea() {
        if (xLength == null || yLength == null) {
            return 0;
        }
        return xLength * yLength;
    }

    /**
     * 1x1 크기인지 확인합니다.
     *
     * @return 1x1 크기인지 여부
     */
    public boolean isDefaultSize() {
        return Integer.valueOf(1).equals(xLength) && Integer.valueOf(1).equals(yLength);
    }

    /**
     * 크기가 정의되어 있는지 확인합니다.
     *
     * @return x,y 모두 null이 아닌지 여부
     */
    public boolean isDefined() {
        return xLength != null && yLength != null;
    }
}