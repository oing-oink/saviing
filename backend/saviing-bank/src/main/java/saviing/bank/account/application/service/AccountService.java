package saviing.bank.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import saviing.bank.account.application.port.in.CreateAccountUseCase;
import saviing.bank.account.application.port.in.command.CreateAccountCommand;
import saviing.bank.account.application.port.in.result.CreateAccountResult;
import saviing.bank.account.application.port.out.GenerateAccountNumberPort;
import saviing.bank.account.application.port.out.SaveAccountPort;
import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.vo.AccountNumber;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements CreateAccountUseCase {

    private final GenerateAccountNumberPort generateAccountNumberPort;
    private final SaveAccountPort saveAccountPort;
    private final ProductService productService;
    
    @Override
    public CreateAccountResult createAccount(CreateAccountCommand command) {
        AccountNumber accountNumber = generateAccountNumberPort.generateUniqueAccountNumber();

        Account account = Account.open(
            accountNumber,
            command.customerId(),
            productService.getProduct(command.productId()),
            Instant.now()
        );

        Account savedAccount = saveAccountPort.save(account);
        return CreateAccountResult.from(savedAccount);
    }
}