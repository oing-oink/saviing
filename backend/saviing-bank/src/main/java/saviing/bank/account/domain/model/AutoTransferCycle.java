package saviing.bank.account.domain.model;

import java.time.DayOfWeek;

/**
 * 자동이체 주기를 표현하는 열거형.
 * WEEKLY(주간), MONTHLY(월간) 두 가지 형태만 지원한다.
 */
public enum AutoTransferCycle {
    WEEKLY,
    MONTHLY;

    /**
     * 주간 주기의 납부일(1~7)을 {@link DayOfWeek} 로 변환한다.
     *
     * @param transferDay 1(월요일)~7(일요일) 범위의 납부 요일
     * @return {@link DayOfWeek} 형태의 요일 정보
     * @throws IllegalStateException 주간 주기가 아닌 경우 호출된 경우
     */
    public DayOfWeek resolveDayOfWeek(int transferDay) {
        if (this != WEEKLY) {
            throw new IllegalStateException("주간 주기가 아닐 때는 요일을 계산할 수 없습니다");
        }
        // transferDay는 1(월)~7(일) 범위를 사용한다.
        return DayOfWeek.of(transferDay);
    }
}
