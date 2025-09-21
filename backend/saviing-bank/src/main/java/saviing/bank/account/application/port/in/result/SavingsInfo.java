package saviing.bank.account.application.port.in.result;

import java.time.Instant;
import java.time.LocalDate;

import lombok.NonNull;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.model.AutoTransferSchedule;

public record SavingsInfo(
    String maturityWithdrawalAccount,
    Long targetAmount,
    Integer termPeriod,
    String termPeriodUnit,
    LocalDate maturityDate,
    AutoTransferInfo autoTransfer
) {

    public static SavingsInfo from(@NonNull Account account) {
        return from(account, null);
    }

    public static SavingsInfo from(@NonNull Account account, AutoTransferSchedule schedule) {
        if (!account.isSavingsAccount()) {
            return null;
        }

        AutoTransferInfo autoTransferInfo = null;
        if (schedule != null) {
            autoTransferInfo = new AutoTransferInfo(
                schedule.isEnabled(),
                schedule.getCycle() != null ? schedule.getCycle().name() : null,
                schedule.getTransferDay(),
                schedule.getAmount() != null ? schedule.getAmount().amount() : null,
                schedule.getNextRunDate(),
                schedule.getLastExecutedAt(),
                schedule.getWithdrawAccountId() != null ? schedule.getWithdrawAccountId().value() : null
            );
        }

        return new SavingsInfo(
            account.getMaturityWithdrawalAccount() != null ?
                account.getMaturityWithdrawalAccount().value() : null,
            account.getTargetAmount().amount(),
            account.getTermPeriod().value(),
            account.getTermPeriod().unit().name(),
            account.getMaturityDate(),
            autoTransferInfo
        );
    }

    /**
     * 자동이체 정보를 표현하는 DTO.
     *
     * @param enabled 자동이체 활성화 여부
     * @param cycle 자동이체 주기
     * @param transferDay 납부 일자
     * @param amount 자동이체 금액
     * @param nextRunDate 다음 실행 예정일
     * @param lastExecutedAt 마지막 실행 시각
     */
    public record AutoTransferInfo(
        boolean enabled,
        String cycle,
        Integer transferDay,
        Long amount,
        LocalDate nextRunDate,
        Instant lastExecutedAt,
        Long withdrawAccountId
    ) {
    }
}
