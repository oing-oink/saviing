package saviing.bank.account.application.port.in.command;

/**
 * 적금 계좌 해지를 요청하기 위한 명령 객체입니다.
 */
public record CloseSavingsAccountCommand(Long accountId) {

    /**
     * 명령 객체를 생성합니다.
     *
     * @param accountId 해지할 계좌 ID
     * @return 해지 명령
     */
    public static CloseSavingsAccountCommand of(Long accountId) {
        return new CloseSavingsAccountCommand(accountId);
    }
}
