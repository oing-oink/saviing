package saviing.bank.account.domain.model;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AutoTransferScheduleId;
import saviing.bank.account.exception.InvalidAccountStateException;
import saviing.bank.account.exception.InvalidAmountException;
import saviing.bank.common.vo.MoneyWon;

/**
 * 적금 자동이체 스케줄 애그리거트.
 * 계좌와 주기, 납부일, 금액을 보존하고 실행 여부에 따라 다음 실행일을 계산한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoTransferSchedule {

    private static final int MIN_WEEKLY_DAY = 1;
    private static final int MAX_WEEKLY_DAY = 7;
    private static final int MIN_MONTHLY_DAY = 1;
    private static final int MAX_MONTHLY_DAY = 31;
    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    private AutoTransferScheduleId id;
    private AccountId accountId;
    private AutoTransferCycle cycle;
    private int transferDay;
    private MoneyWon amount;
    private boolean enabled;
    private LocalDate nextRunDate;
    private Instant lastExecutedAt;
    private Instant createdAt;
    private Instant updatedAt;

    private AutoTransferSchedule(
        @NonNull AccountId accountId,
        @NonNull AutoTransferCycle cycle,
        int transferDay,
        @NonNull MoneyWon amount,
        boolean enabled,
        LocalDate today,
        @NonNull Instant now
    ) {
        this.accountId = accountId;
        this.cycle = Objects.requireNonNull(cycle, "주기는 필수입니다");
        this.transferDay = validateTransferDay(cycle, transferDay);
        this.amount = validateAmount(amount);
        this.enabled = enabled;
        this.lastExecutedAt = null;
        this.nextRunDate = enabled ? calculateNextRunDate(today, cycle, transferDay, false) : null;
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 새로운 자동이체 스케줄을 생성한다.
     *
     * @param accountId 자동이체를 수행할 계좌 식별자
     * @param cycle 자동이체 주기
     * @param transferDay 납부 일자(주간은 1~7, 월간은 1~31)
     * @param amount 납부 금액
     * @param enabled 자동이체 활성화 여부
     * @param today 실행 기준일(오늘 날짜)
     * @param now 시스템 시각(생성 일시 기록용)
     * @return 생성된 자동이체 스케줄
     */
    public static AutoTransferSchedule create(
        @NonNull AccountId accountId,
        @NonNull AutoTransferCycle cycle,
        int transferDay,
        @NonNull MoneyWon amount,
        boolean enabled,
        @NonNull LocalDate today,
        @NonNull Instant now
    ) {
        return new AutoTransferSchedule(accountId, cycle, transferDay, amount, enabled, today, now);
    }

    /**
     * 저장된 데이터를 통해 자동이체 스케줄을 복원한다.
     *
     * @param id 자동이체 스케줄 식별자
     * @param accountId 계좌 식별자
     * @param cycle 자동이체 주기
     * @param transferDay 납부 일자(주간은 1~7, 월간은 1~31)
     * @param amount 납부 금액
     * @param enabled 자동이체 활성화 여부
     * @param nextRunDate 다음 실행 예정일
     * @param lastExecutedAt 마지막 실행 시각(없으면 null)
     * @param createdAt 생성 일시
     * @param updatedAt 최근 수정 일시
     * @return 복원된 자동이체 스케줄
     */
    public static AutoTransferSchedule restore(
        AutoTransferScheduleId id,
        @NonNull AccountId accountId,
        @NonNull AutoTransferCycle cycle,
        int transferDay,
        @NonNull MoneyWon amount,
        boolean enabled,
        LocalDate nextRunDate,
        Instant lastExecutedAt,
        @NonNull Instant createdAt,
        @NonNull Instant updatedAt
    ) {
        AutoTransferSchedule schedule = new AutoTransferSchedule();
        schedule.id = id;
        schedule.accountId = accountId;
        schedule.cycle = cycle;
        schedule.transferDay = validateTransferDay(cycle, transferDay);
        schedule.amount = validateAmount(amount);
        schedule.enabled = enabled;
        schedule.nextRunDate = nextRunDate;
        schedule.lastExecutedAt = lastExecutedAt;
        schedule.createdAt = createdAt;
        schedule.updatedAt = updatedAt;
        return schedule;
    }

    /**
     * 자동이체 설정을 갱신한다.
     * 이미 해당 주/월에 납부된 상태라면 다음 사이클부터 적용되도록 nextRunDate를 이동한다.
     * 이번 주/월에서 새 납부 일자가 이미 지난 경우 기존 nextRunDate 유지하고 다음 주/월부터 적용한다.
     *
     * @param cycle 새 자동이체 주기
     * @param transferDay 새 납부 일자
     * @param amount 새 납부 금액
     * @param enabled 자동이체 활성화 여부
     * @param today 기준 날짜(오늘)
     * @param now 수정 일시
     */
    public void update(
        @NonNull AutoTransferCycle cycle,
        int transferDay,
        @NonNull MoneyWon amount,
        boolean enabled,
        @NonNull LocalDate today,
        @NonNull Instant now
    ) {
        ensureActive();

        this.cycle = cycle;
        this.transferDay = validateTransferDay(cycle, transferDay);
        this.amount = validateAmount(amount);
        this.enabled = enabled;

        // 다음 실행일을 계산한다.
        if (!enabled) {
            this.nextRunDate = null;
        } else {
            boolean alreadyExecuted = hasExecutedForCurrentCycle(today);
            boolean alreadyPassedInCycle = hasPassedScheduledDayToday(today, cycle, transferDay);

            // 이미 납부된 주/월이면 다음 주/월부터 적용
            // 새로 설정한 납부일이 오늘 날짜보다 이전이면 이번 주/월의 실행 계획은 유지하고 다음 주/월부터 적용

            if (nextRunDate == null || alreadyExecuted || !alreadyPassedInCycle) {
                this.nextRunDate = calculateNextRunDate(today, cycle, transferDay, alreadyExecuted);
            }
        }

        this.updatedAt = now;
    }

    /**
     * 자동이체 실행을 기록하고 다음 실행일을 계산한다.
     *
     * @param executedAt 자동이체가 실제로 실행된 시각
     */
    public void markExecuted(@NonNull Instant executedAt) {
        if (!enabled) {
            throw new InvalidAccountStateException(Map.of(
                "accountId", accountId != null ? accountId.value() : null,
                "reason", "AUTO_TRANSFER_DISABLED"
            ));
        }
        this.lastExecutedAt = executedAt;
        LocalDate executionDate = executedAt.atZone(SYSTEM_ZONE).toLocalDate();
        this.nextRunDate = calculateNextRunDate(executionDate.plusDays(1), cycle, transferDay, false);
        this.updatedAt = executedAt;
    }

    /**
     * 오늘 기준으로 이번 주/월에 이미 납부했는지 확인한다.
     *
     * @param today 기준 날짜(오늘)
     * @return 이번 주/월에 이미 실행된 경우 true
     */
    public boolean hasExecutedForCurrentCycle(@NonNull LocalDate today) {
        if (lastExecutedAt == null) {
            return false;
        }

        LocalDate executedDate = lastExecutedAt.atZone(SYSTEM_ZONE).toLocalDate();
        return switch (cycle) {
            case WEEKLY -> isSameWeek(executedDate, today);
            case MONTHLY -> executedDate.getYear() == today.getYear()
                && executedDate.getMonthValue() == today.getMonthValue();
        };
    }

    /**
     * 납부 금액을 검증한다.
     *
     * @param amount 검증할 금액
     * @return 양수 금액인 경우 그대로 반환
     * @throws InvalidAmountException 0 이하 금액이 입력된 경우
     */
    private static MoneyWon validateAmount(MoneyWon amount) {
        if (!amount.isPositive()) {
            throw new InvalidAmountException(Map.of("amount", amount.amount()));
        }
        return amount;
    }

    /**
     * 주기별 납부 일자 범위를 검증한다.
     *
     * @param cycle 자동이체 주기
     * @param transferDay 설정할 납부 일자
     * @return 검증된 납부 일자
     * @throws IllegalArgumentException 허용 범위를 벗어난 경우
     */
    private static int validateTransferDay(AutoTransferCycle cycle, int transferDay) {
        if (cycle == AutoTransferCycle.WEEKLY) {
            if (transferDay < MIN_WEEKLY_DAY || transferDay > MAX_WEEKLY_DAY) {
                throw new IllegalArgumentException("주간 자동이체 요일은 1~7 범위여야 합니다");
            }
            return transferDay;
        }
        if (transferDay < MIN_MONTHLY_DAY || transferDay > MAX_MONTHLY_DAY) {
            throw new IllegalArgumentException("월간 자동이체 일자는 1~31 범위여야 합니다");
        }
        return transferDay;
    }

    /**
     * 다음 실행 예정일을 계산한다.
     *
     * @param referenceDate 기준 날짜
     * @param cycle 자동이체 주기
     * @param transferDay 납부 일자
     * @param moveToNextCycle 다음 사이클로 이동해야 하는지 여부
     * @return 다음 실행 예정일
     */
    private static LocalDate calculateNextRunDate(
        LocalDate referenceDate,
        AutoTransferCycle cycle,
        int transferDay,
        boolean moveToNextCycle
    ) {
        return switch (cycle) {
            case WEEKLY -> calculateNextWeeklyDate(referenceDate, transferDay, moveToNextCycle);
            case MONTHLY -> calculateNextMonthlyDate(referenceDate, transferDay, moveToNextCycle);
        };
    }

    /**
     * 주간 자동이체의 다음 실행일을 계산한다.
     *
     * @param reference 기준 날짜
     * @param transferDay 납부 요일(1~7)
     * @param moveToNextCycle 다음 주로 이동해야 하는지 여부
     * @return 주간 자동이체 다음 실행일
     */
    private static LocalDate calculateNextWeeklyDate(LocalDate reference, int transferDay, boolean moveToNextCycle) {
        DayOfWeek targetDay = DayOfWeek.of(transferDay);
        int diff = targetDay.getValue() - reference.getDayOfWeek().getValue();
        if (diff < 0 || moveToNextCycle) {
            diff += 7;
        }
        return reference.plusDays(diff);
    }

    /**
     * 월간 자동이체의 다음 실행일을 계산한다.
     *
     * @param reference 기준 날짜
     * @param transferDay 납부 일자(1~31)
     * @param moveToNextCycle 다음 달로 이동해야 하는지 여부
     * @return 월간 자동이체 다음 실행일
     */
    private static LocalDate calculateNextMonthlyDate(LocalDate reference, int transferDay, boolean moveToNextCycle) {
        LocalDate candidate = adjustMonthlyDate(reference, transferDay);
        if (candidate.isBefore(reference) || moveToNextCycle) {
            LocalDate nextMonth = reference.plusMonths(1).withDayOfMonth(1);
            candidate = adjustMonthlyDate(nextMonth, transferDay);
        }
        return candidate;
    }

    /**
     * 월간 자동이체 일자를 해당 달에 맞게 조정한다.
     *
     * @param reference 기준 날짜
     * @param transferDay 납부 일자(1~31)
     * @return 조정된 날짜 (말일 초과 시 해당 달의 마지막 날)
     */
    private static LocalDate adjustMonthlyDate(LocalDate reference, int transferDay) {
        int day = Math.min(transferDay, reference.lengthOfMonth());
        return reference.withDayOfMonth(day);
    }

    /**
     * 두 날짜가 같은 ISO 주차에 속하는지 판단한다.
     *
     * @param executedDate 실행이 기록된 날짜
     * @param today 비교할 오늘 날짜
     * @return 동일한 주에 속하면 true
     */
    private static boolean isSameWeek(LocalDate executedDate, LocalDate today) {
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return (executedDate.isEqual(startOfWeek) || executedDate.isAfter(startOfWeek))
            && (executedDate.isBefore(endOfWeek) || executedDate.isEqual(endOfWeek));
    }

    /**
     * 오늘 기준으로 새로 설정한 납부일이 이미 지난 날인지 확인한다.
     *
     * @param today 기준 날짜
     * @param cycle 자동이체 주기
     * @param transferDay 새 납부 일자
     * @return 이미 지난 날이면 true
     */
    private static boolean hasPassedScheduledDayToday(LocalDate today, AutoTransferCycle cycle, int transferDay) {
        return switch (cycle) {
            case WEEKLY -> DayOfWeek.of(transferDay).getValue() < today.getDayOfWeek().getValue();
            case MONTHLY -> adjustMonthlyDate(today, transferDay).isBefore(today);
        };
    }

    /**
     * 스케줄이 계좌에 연결되어 있는지 확인한다.
     *
     * @throws InvalidAccountStateException 계좌 정보가 없는 경우
     */
    private void ensureActive() {
        if (accountId == null) {
            throw new InvalidAccountStateException(Map.of("reason", "DETACHED_AUTO_TRANSFER"));
        }
    }
}
