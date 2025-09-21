package saviing.bank.account.application.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import saviing.bank.account.application.port.out.AutoTransferSchedulePort;
import saviing.bank.account.domain.model.AutoTransferSchedule;
import saviing.bank.account.domain.vo.AutoTransferScheduleId;
import saviing.bank.transaction.application.service.TransferService;
import saviing.bank.transaction.application.port.in.command.TransferCommand;
import saviing.bank.transaction.domain.model.transfer.TransferType;
import saviing.bank.transaction.domain.vo.IdempotencyKey;

/**
 * 적금 자동이체를 定기적으로 실행하는 스케줄러.
 * Spring {@code @Scheduled}를 사용해 매일 지정된 시간에 실행 대상 스케줄을 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTransferScheduler {

    private final TransactionTemplate txTemplate;
    private final AutoTransferSchedulePort autoTransferSchedulePort;
    private final TransferService transferService;
    @Value("${auto-transfer.scheduler.demo-reset-enabled:false}")
    private boolean demoResetEnabled;

    /**
     * 자동이체 스케줄을 실행한다.
     *
     * @implNote 기본 실행 주기는 매분 실행되며, {@code auto-transfer.scheduler.cron} 프로퍼티로 조정할 수 있다.
     */
    @Scheduled(cron = "${auto-transfer.scheduler.cron:0 */1 * * * *}")
    public void processAutoTransferSchedules() {
        LocalDate today = LocalDate.now();
        if (demoResetEnabled) {
            txTemplate.executeWithoutResult(status -> autoTransferSchedulePort.resetAllNextRunDate(today));
        }
        List<AutoTransferScheduleId> scheduleIds = autoTransferSchedulePort.findDueSchedulesIds(today);
        if (scheduleIds.isEmpty()) {
            log.info("처리할 자동이체 스케줄이 없습니다. date={}", today);
            return;
        }

        log.info("{}건의 자동이체 스케줄을 처리합니다. date={}", scheduleIds.size(), today);
        for (AutoTransferScheduleId scheduleId : scheduleIds) {
            txTemplate.executeWithoutResult(status -> {
                try {
                    AutoTransferSchedule schedule = autoTransferSchedulePort.findByIdForUpdate(scheduleId)
                        .orElseThrow(() -> new IllegalStateException("자동이체 스케줄을 찾을 수 없습니다: " + scheduleId.value()));
                    executeSchedule(schedule);
                } catch (Exception ex) {
                    status.setRollbackOnly();
                    log.error("자동이체 실행에 실패했습니다. scheduleId={}, message={}",
                        scheduleId.value(),
                        ex.getMessage(),
                        ex
                    );
                }
            });
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

        if (schedule.getWithdrawAccountId() == null) {
            log.warn("출금 계좌가 설정되지 않은 자동이체를 건너뜁니다. savingsAccountId={}", schedule.getAccountId().value());
            return;
        }

        IdempotencyKey idempotencyKey = IdempotencyKey.of("auto-transfer-%d-%s".formatted(schedule.getId().value(), Instant.now()));

        TransferCommand command = TransferCommand.builder()
            .sourceAccountId(schedule.getWithdrawAccountId().value())
            .targetAccountId(schedule.getAccountId().value())
            .amount(schedule.getAmount())
            .valueDate(LocalDate.now())
            .transferType(TransferType.INTERNAL)
            .memo("적금 자동 이체")
            .idempotencyKey(idempotencyKey)
            .build();

        transferService.transfer(command);

        Instant executedAt = Instant.now();
        schedule.markExecuted(executedAt);
        autoTransferSchedulePort.update(schedule);

        log.info("자동이체 실행 완료 savingsAccountId={}, withdrawAccountId={}, amount={}, nextRunDate={}",
            schedule.getAccountId().value(),
            schedule.getWithdrawAccountId().value(),
            schedule.getAmount().amount(),
            schedule.getNextRunDate()
        );
    }
}
