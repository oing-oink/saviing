package saviing.bank.account.application.port.in.result;

import java.time.LocalDate;

import lombok.NonNull;
import saviing.bank.account.domain.model.Account;

public record SavingsInfo(
    String maturityWithdrawalAccount,
    Long targetAmount,
    Integer termPeriod,
    String termPeriodUnit,
    LocalDate maturityDate
) {

    public static SavingsInfo from(@NonNull Account account) {
        if (!account.isSavingsAccount()) {
            return null;
        }

        return new SavingsInfo(
            account.getMaturityWithdrawalAccount() != null ?
                account.getMaturityWithdrawalAccount().value() : null,
            account.getTargetAmount().amount(),
            account.getTermPeriod().value(),
            account.getTermPeriod().unit().name(),
            account.getMaturityDate()
        );
    }
}