package saviing.bank.account.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import saviing.bank.account.domain.model.AutoTransferCycle;
import saviing.bank.account.domain.model.AutoTransferSchedule;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AutoTransferScheduleId;
import saviing.bank.common.vo.MoneyWon;

/**
 * 자동이체 스케줄을 저장하는 JPA 엔티티.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "account_auto_transfer",
    indexes = {
        @Index(name = "idx_auto_transfer_account_id", columnList = "account_id"),
        @Index(name = "idx_auto_transfer_next_run", columnList = "enabled, next_run_date")
    }
)
public class AutoTransferScheduleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_transfer_id")
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "cycle", nullable = false)
    private AutoTransferCycle cycle;

    @Column(name = "transfer_day", nullable = false)
    private Integer transferDay;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "next_run_date")
    private LocalDate nextRunDate;

    @Column(name = "last_executed_at")
    private Instant lastExecutedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * 도메인 모델을 엔티티로 변환한다.
     *
     * @param schedule 변환할 자동이체 스케줄 도메인 모델
     * @return 변환된 JPA 엔티티
     */
    public static AutoTransferScheduleJpaEntity fromDomain(AutoTransferSchedule schedule) {
        AutoTransferScheduleJpaEntity entity = new AutoTransferScheduleJpaEntity();
        if (schedule.getId() != null) {
            entity.id = schedule.getId().value();
        }
        entity.accountId = schedule.getAccountId().value();
        entity.cycle = schedule.getCycle();
        entity.transferDay = schedule.getTransferDay();
        entity.amount = schedule.getAmount().amount();
        entity.enabled = schedule.isEnabled();
        entity.nextRunDate = schedule.getNextRunDate();
        entity.lastExecutedAt = schedule.getLastExecutedAt();
        entity.createdAt = schedule.getCreatedAt();
        entity.updatedAt = schedule.getUpdatedAt();
        return entity;
    }

    /**
     * JPA 엔티티를 도메인 모델로 변환한다.
     *
     * @return 변환된 자동이체 스케줄 도메인 모델
     */
    public AutoTransferSchedule toDomain() {
        return AutoTransferSchedule.restore(
            id != null ? AutoTransferScheduleId.of(id) : null,
            AccountId.of(accountId),
            cycle,
            transferDay,
            MoneyWon.of(amount),
            enabled,
            nextRunDate,
            lastExecutedAt,
            createdAt,
            updatedAt
        );
    }

    /**
     * 도메인 모델의 변경사항을 엔티티에 반영한다.
     *
     * @param schedule 최신 자동이체 스케줄 도메인 모델
     */
    public void updateFromDomain(AutoTransferSchedule schedule) {
        this.cycle = schedule.getCycle();
        this.transferDay = schedule.getTransferDay();
        this.amount = schedule.getAmount().amount();
        this.enabled = schedule.isEnabled();
        this.nextRunDate = schedule.getNextRunDate();
        this.lastExecutedAt = schedule.getLastExecutedAt();
        this.updatedAt = schedule.getUpdatedAt();
    }
}
