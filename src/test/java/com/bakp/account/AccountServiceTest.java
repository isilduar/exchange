package com.bakp.account;

import com.bakp.TestBase;
import com.bakp.dto.AccountDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bakp.exchange.Currency.PLN;
import static com.bakp.exchange.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountServiceTest extends TestBase {

    @Autowired
    private AccountService accountService;

    @Test
    public void shouldTransferMoneyBetweenAccounts() {
        final AccountDto plnAccount = accountService.createAccount(PLN);
        final AccountDto usdAccount = accountService.createAccount(USD);
        final float amount = 100;
        accountService.addMoney(plnAccount.getId(), amount);

        accountService.transferMoney(plnAccount.getId(), usdAccount.getId(), amount, PLN);

        Account usdAccountAfterMoneyTransfer = this.accountRepository.getById(usdAccount.getId());
        assertThat(usdAccountAfterMoneyTransfer).isNotNull();
        assertThat(usdAccountAfterMoneyTransfer.getBalance()).isGreaterThan(0F);

        Account plnAccountAfterMoneyTransfer = this.accountRepository.getById(plnAccount.getId());
        assertThat(plnAccountAfterMoneyTransfer).isNotNull();
        assertThat(plnAccountAfterMoneyTransfer.getBalance()).isEqualTo(0.0F);
    }

    @Test
    public void shouldNotTransferMoneyBetweenAccountsWhenThereIsNoMoney() {
        final AccountDto plnAccount = accountService.createAccount(PLN);
        final AccountDto usdAccount = accountService.createAccount(USD);

        assertThrows(RuntimeException.class, () -> accountService.transferMoney(plnAccount.getId(), usdAccount.getId(), 100, PLN));
    }
}
