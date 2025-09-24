package saviing.game.room.domain.model.vo;

import java.util.Objects;

/**
 * 방 내 아이템 크기를 나타내는 값 객체
 * 격자 기반의 X축 길이와 Y축 길이를 표현
 *
 * @param xLength X축 길이 (1 이상)
 * @param yLength Y축 길이 (1 이상)
 */
public record ItemSize(int xLength, int yLength) {

    /**
     * ItemSize 생성자
     *
     * @param xLength X축 길이
     * @param yLength Y축 길이
     * @throws IllegalArgumentException xLength 또는 yLength가 1보다 작은 경우
     */
    public ItemSize {
        if (xLength < 1 || yLength < 1) {
            throw new IllegalArgumentException("ItemSize dimensions must be positive");
        }
    }

    /**
     * 크기의 면적을 계산
     *
     * @return xLength * yLength
     */
    public int area() {
        return xLength * yLength;
    }

    /**
     * 특정 위치에서 이 크기로 배치했을 때 차지하는 끝 좌표를 계산
     *
     * @param position 시작 위치
     * @return 끝 좌표 (exclusive)
     * @throws IllegalArgumentException position이 null인 경우
     */
    public Position endPositionFrom(Position position) {
        Objects.requireNonNull(position, "position");
        return new Position(position.x() + xLength, position.y() + yLength);
    }
}