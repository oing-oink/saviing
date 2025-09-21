package saviing.bank.account.application.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import saviing.bank.account.application.port.out.AutoTransferSchedulePort;
import saviing.bank.account.domain.model.AutoTransferSchedule;

/**
 * 적금 자동이체를 定기적으로 실행하는 스케줄러.
 * Spring {@code @Scheduled}를 사용해 매일 지정된 시간에 실행 대상 스케줄을 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTransferScheduler {

    private final AutoTransferSchedulePort autoTransferSchedulePort;
    private final AccountBalanceService accountBalanceService;

    /**
     * 자동이체 스케줄을 실행한다.
     *
     * @implNote 기본 실행 주기는 매분 실행되며, {@code auto-transfer.scheduler.cron} 프로퍼티로 조정할 수 있다.
     */
    @Scheduled(cron = "${auto-transfer.scheduler.cron:0 */1 * * * *}")
    @Transactional
    public void processAutoTransferSchedules() {
        LocalDate today = LocalDate.now();
        List<AutoTransferSchedule> schedules = autoTransferSchedulePort.findDueSchedules(today);
        if (schedules.isEmpty()) {
            log.info("처리할 자동이체 스케줄이 없습니다. date={}", today);
            return;
        }

        log.info("{}건의 자동이체 스케줄을 처리합니다. date={}", schedules.size(), today);
        for (AutoTransferSchedule schedule : schedules) {
            try {
                executeSchedule(schedule);
            } catch (Exception ex) {
                log.error("자동이체 실행에 실패했습니다. accountId={}, scheduleId={}, message={}",
                    schedule.getAccountId().value(),
                    schedule.getId() != null ? schedule.getId().value() : null,
                    ex.getMessage(),
                    ex
                );
            }
        }
    }

    /**
     * 단일 자동이체 스케줄을 실행한다.
     *
     * @param schedule 실행할 자동이체 스케줄
     */
    private void executeSchedule(AutoTransferSchedule schedule) {
        if (!schedule.isEnabled()) {
            return;
        }

        Instant executedAt = Instant.now();
        accountBalanceService.deposit(
            schedule.getAccountId().value(),
            schedule.getAmount().amount()
        );
        schedule.markExecuted(executedAt);
        autoTransferSchedulePort.update(schedule);

        log.info("자동이체 실행 완료 accountId={}, amount={}, nextRunDate={}",
            schedule.getAccountId().value(),
            schedule.getAmount().amount(),
            schedule.getNextRunDate()
        );
    }
}
