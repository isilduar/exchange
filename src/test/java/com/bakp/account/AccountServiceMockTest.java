package com.bakp.account;

import com.bakp.TestBase;
import com.bakp.dto.AccountDto;
import com.bakp.exchange.ExchangeClient;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.bakp.exchange.Currency.PLN;
import static com.bakp.exchange.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

public class AccountServiceMockTest extends TestBase {

    @MockBean
    private ExchangeClient exchangeClient;

    @Autowired
    private AccountService accountService;

    @Test
    public void shouldNotTransferMoneyBetweenAccountsWhenThereIsNoPossibilityToGetCurrencyData() {
        doThrow(FeignException.class).when(exchangeClient).convertFromToWithPlaces(anyString(), anyString(), anyFloat(), anyInt());

        final AccountDto plnAccount = accountService.createAccount(PLN);
        final AccountDto usdAccount = accountService.createAccount(USD);
        final float moneyToTransfer = 100;
        accountService.addMoney(plnAccount.getId(), moneyToTransfer);

        assertThrows(FeignException.class, () -> accountService.transferMoney(plnAccount.getId(), usdAccount.getId(), 100, PLN));

        final Account plnAccountAfterFailedTransfer = accountRepository.getById(plnAccount.getId());
        assertThat(plnAccountAfterFailedTransfer).isNotNull();
        assertThat(plnAccountAfterFailedTransfer.getBalance()).isEqualTo(moneyToTransfer);
        final Account usdAccountAfterFailedTransfer = accountRepository.getById(usdAccount.getId());
        assertThat(usdAccountAfterFailedTransfer).isNotNull();
        assertThat(usdAccountAfterFailedTransfer.getBalance()).isEqualTo(0);
    }
}
