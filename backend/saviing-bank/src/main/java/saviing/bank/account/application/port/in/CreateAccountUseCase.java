package saviing.bank.account.application.port.in;

import saviing.bank.account.application.port.in.command.CreateAccountCommand;
import saviing.bank.account.application.port.in.result.CreateAccountResult;

public interface CreateAccountUseCase {

    /**
     * 계좌 생성 요청을 처리합니다.
     *
     * Pattern Matching을 통해 상품 카테고리에 따른 분기 처리를 합니다.
     *
     * @param command 계좌 생성 명령 (CreateDemandDepositCommand 또는 CreateSavingsCommand)
     * @return 생성된 계좌 정보
     * @throws InvalidProductTypeException Command와 상품 타입이 일치하지 않는 경우
     */
    CreateAccountResult createAccount(CreateAccountCommand command);
}