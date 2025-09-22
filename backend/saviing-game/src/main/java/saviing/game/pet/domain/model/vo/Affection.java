package saviing.game.pet.domain.model.vo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 펫 애정도 Value Object
 * 10분마다 1씩 감소하는 시간 기반 감소 시스템
 */
public record Affection(int value) {
    private static final int MIN_AFFECTION = 0;
    private static final int MAX_AFFECTION = 100;
    private static final int INITIAL_AFFECTION = 50;
    private static final int DECAY_INTERVAL_MINUTES = 10;
    private static final int DECAY_AMOUNT_PER_INTERVAL = 1;

    public Affection {
        if (value < MIN_AFFECTION || value > MAX_AFFECTION) {
            throw new IllegalArgumentException(
                String.format("펫 애정도는 %d와 %d 사이여야 합니다. 입력값: %d", MIN_AFFECTION, MAX_AFFECTION, value)
            );
        }
    }

    public static Affection of(int affection) {
        return new Affection(affection);
    }

    public static Affection initial() {
        return new Affection(INITIAL_AFFECTION);
    }

    public static Affection max() {
        return new Affection(MAX_AFFECTION);
    }

    public static Affection min() {
        return new Affection(MIN_AFFECTION);
    }

    public Affection increase(Affection amount) {
        if (amount.value < 0) {
            throw new IllegalArgumentException("애정도 증가량은 음수일 수 없습니다");
        }
        int newValue = Math.min(MAX_AFFECTION, value + amount.value);
        return new Affection(newValue);
    }

    public Affection decrease(Affection amount) {
        if (amount.value < 0) {
            throw new IllegalArgumentException("애정도 감소량은 음수일 수 없습니다");
        }
        int newValue = Math.max(MIN_AFFECTION, value - amount.value);
        return new Affection(newValue);
    }

    /**
     * 마지막 접속 시간을 기준으로 시간 경과에 따른 애정도 자동 감소를 적용합니다.
     *
     * <p><strong>감소 규칙:</strong></p>
     * <ul>
     *   <li>10분마다 애정도 1씩 감소</li>
     *   <li>10분 미만 경과 시에는 감소하지 않음</li>
     *   <li>애정도는 0 미만으로 떨어지지 않음</li>
     * </ul>
     *
     * <p><strong>계산 예시:</strong></p>
     * <ul>
     *   <li>5분 경과: 감소 없음 (10분 미만)</li>
     *   <li>15분 경과: 1 감소 (10분 구간 1개)</li>
     *   <li>25분 경과: 2 감소 (10분 구간 2개)</li>
     *   <li>100분 경과: 10 감소 (10분 구간 10개)</li>
     * </ul>
     *
     * <p><strong>주의사항:</strong></p>
     * <ul>
     *   <li>lastAccessTime이 null인 경우: 현재 애정도 그대로 반환</li>
     *   <li>currentTime이 null인 경우: 현재 애정도 그대로 반환</li>
     *   <li>lastAccessTime이 currentTime보다 미래인 경우: 음수 시간으로 처리되어 감소 없음</li>
     * </ul>
     *
     * @param lastAccessTime 마지막 접속 시간 (캐릭터의 last_access_at)
     * @param currentTime 현재 시간 (일반적으로 LocalDateTime.now())
     * @return 시간 경과에 따라 감소가 적용된 새로운 Affection 객체
     */
    public Affection applyTimeDecay(LocalDateTime lastAccessTime, LocalDateTime currentTime) {
        // null 체크: 시간 정보가 없으면 현재 상태 유지
        if (lastAccessTime == null || currentTime == null) {
            return this;
        }

        // 경과 시간을 분 단위로 계산
        long minutesPassed = ChronoUnit.MINUTES.between(lastAccessTime, currentTime);

        // 10분 미만 경과 시에는 감소하지 않음
        if (minutesPassed < DECAY_INTERVAL_MINUTES) {
            return this;
        }

        // 10분 단위로 몇 번의 감소 구간이 지났는지 계산
        // 예: 25분 경과 시 25 / 10 = 2구간 (소수점 버림)
        long decayIntervals = minutesPassed / DECAY_INTERVAL_MINUTES;

        // 총 감소량 계산 (구간 수 × 구간당 감소량)
        int totalDecay = (int) (decayIntervals * DECAY_AMOUNT_PER_INTERVAL);

        // 감소 적용 (0 미만으로는 떨어지지 않음)
        return decrease(Affection.of(totalDecay));
    }

    public boolean isMaxAffection() {
        return value == MAX_AFFECTION;
    }

    public boolean isMinAffection() {
        return value == MIN_AFFECTION;
    }

    /**
     * 애정도 범위를 반환합니다 (배수 계산용)
     * 0-25: LOW, 26-50: MEDIUM, 51-75: HIGH, 76-100: VERY_HIGH
     */
    public AffectionRange getRange() {
        if (value <= 25) return AffectionRange.LOW;
        if (value <= 50) return AffectionRange.MEDIUM;
        if (value <= 75) return AffectionRange.HIGH;
        return AffectionRange.VERY_HIGH;
    }

    public enum AffectionRange {
        LOW, MEDIUM, HIGH, VERY_HIGH
    }
}