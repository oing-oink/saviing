package saviing.bank.account.application.port.in;

import saviing.bank.account.application.port.in.command.CreateAccountCommand;
import saviing.bank.account.application.port.in.result.CreateAccountResult;

public interface CreateAccountUseCase {
    CreateAccountResult createAccount(CreateAccountCommand command);
}