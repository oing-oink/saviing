package saviing.game.room.domain.model.vo;

/**
 * 방 번호를 나타내는 Value Object
 * 1부터 5까지의 범위 내에서 방 번호를 관리하며,
 * 각 캐릭터는 방 번호별로 고유한 방을 가질 수 있다.
 *
 * @param value 방 번호 (1-5 범위의 byte 값)
 */
public record RoomNumber(byte value) {

    /**
     * 기본 방 번호 (1번)
     * 캐릭터 생성 시 자동으로 생성되는 첫 번째 방의 번호
     */
    public static final RoomNumber DEFAULT = new RoomNumber((byte) 1);

    private static final byte MIN_VALUE = 1;
    private static final byte MAX_VALUE = 5;

    /**
     * RoomNumber compact 생성자
     * 방 번호의 유효성을 검증하고 1-5 범위를 벗어나면 예외를 발생시킨다.
     *
     * @throws IllegalArgumentException value가 1-5 범위를 벗어나는 경우
     */
    public RoomNumber {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException(
                String.format("방 번호는 %d부터 %d까지 가능합니다: %d", MIN_VALUE, MAX_VALUE, value)
            );
        }
    }

    /**
     * 정적 팩토리 메서드로 RoomNumber 인스턴스를 생성
     *
     * @param value 방 번호 (1-5 범위의 byte 값)
     * @return 생성된 RoomNumber 인스턴스
     * @throws IllegalArgumentException value가 1-5 범위를 벗어나는 경우
     */
    public static RoomNumber of(byte value) {
        return new RoomNumber(value);
    }
}