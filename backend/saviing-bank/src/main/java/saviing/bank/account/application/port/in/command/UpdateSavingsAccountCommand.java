package saviing.bank.account.application.port.in.command;

/**
 * 적금 계좌의 목표 금액과 만기 출금 계좌를 수정하기 위한 명령 객체입니다.
 * 필드가 null이면 해당 값은 변경하지 않습니다.
 */
public record UpdateSavingsAccountCommand(
    Long accountId,
    Long targetAmount,
    String maturityWithdrawalAccount
) {

    /**
     * 명령 객체를 생성합니다.
     *
     * @param accountId 수정할 계좌 ID
     * @param targetAmount 변경할 목표 금액 (null 허용)
     * @param maturityWithdrawalAccount 변경할 만기 출금 계좌번호 (null 허용)
     * @return 수정 명령
     */
    public static UpdateSavingsAccountCommand of(
        Long accountId,
        Long targetAmount,
        String maturityWithdrawalAccount
    ) {
        return new UpdateSavingsAccountCommand(accountId, targetAmount, maturityWithdrawalAccount);
    }
}
