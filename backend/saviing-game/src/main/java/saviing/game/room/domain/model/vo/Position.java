package saviing.game.room.domain.model.vo;

import lombok.NonNull;

/**
 * 방 내 아이템 배치 위치를 나타내는 값 객체
 * 격자 기반의 X, Y 좌표를 표현
 *
 * @param x X 좌표 (0 이상)
 * @param y Y 좌표 (0 이상)
 */
public record Position(int x, int y) {

    /**
     * Position 생성자
     *
     * @param x X 좌표
     * @param y Y 좌표
     * @throws IllegalArgumentException x 또는 y가 음수인 경우
     */
    public Position {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Position coordinates must be non-negative");
        }
    }

    /**
     * 다른 위치까지의 맨하탄 거리를 계산
     *
     * @param other 대상 위치
     * @return 맨하탄 거리
     * @throws IllegalArgumentException other가 null인 경우
     */
    public int manhattanDistanceTo(@NonNull Position other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }
}