package com.bakp.account;

import com.bakp.dto.AccountDto;
import com.bakp.dto.AccountTransferDto;
import com.bakp.dto.ExchangeResultDto;
import com.bakp.exchange.Currency;
import com.bakp.exchange.ExchangeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    @Value("${exchange.places:2}")
    private int places;

    private final ExchangeClient exchangeClient;
    private final AccountConverter accountConverter;
    private final AccountRepository accountRepository;

    public AccountDto createAccount(final Currency currency) {
        return accountConverter.convert(accountRepository.save(Account.builder()
                .currency(currency)
                .build()));
    }

    public AccountDto findAccount(final long id) {
        return accountConverter.convert(accountRepository.getById(id));
    }

    public AccountDto addMoney(final long id, final float amount) {
        validateAmount(amount);
        final Account accountData = accountRepository.getById(id);
        accountData.setBalance(accountData.getBalance() + amount);
        return accountConverter.convert(accountData);
    }

    public AccountDto removeMoney(final long id, final float amount) {
        validateAmount(amount);
        final Account accountData = accountRepository.getById(id);
        final float newBalance = accountData.getBalance() - amount;
        validateAmount(newBalance);
        accountData.setBalance(newBalance);
        return accountConverter.convert(accountData);
    }

    public AccountTransferDto transferMoney(final long fromId, final long toId, final float amount, final Currency currency) {
        final Account fromAccount = accountRepository.getById(fromId);
        final Account toAccount = accountRepository.getById(toId);
        return transferMoney(fromAccount, toAccount, amount, currency);
    }

    public void removeAccount(final long id) {
        accountRepository.deleteById(id);
    }

    private AccountTransferDto transferMoney(final Account fromAccount, final Account toAccount, final float amount, final Currency currency) {
        Currency toCurrency = toAccount.getCurrency();
        Currency fromCurrency = fromAccount.getCurrency();
        final ExchangeResultDto fromToExchangeResult = exchangeClient.convertFromToWithPlaces(fromCurrency.name(), toCurrency.name(), amount, places);
        float moneyToTransferAfterExchange = moneyToTransfer(fromCurrency, currency, amount);
        validateBalance(fromAccount, moneyToTransferAfterExchange);
        fromAccount.setBalance(fromAccount.getBalance() - moneyToTransferAfterExchange);
        toAccount.setBalance(toAccount.getBalance() + fromToExchangeResult.getResult());
        return AccountTransferDto.builder()
                .from(accountConverter.convert(fromAccount))
                .to(accountConverter.convert(toAccount))
                .build();
    }

    private float moneyToTransfer(final Currency fromCurrency, final Currency currency, final float amount) {
        ExchangeResultDto possibilityToTransferExchangeResult;
        if (fromCurrency != currency) {
            possibilityToTransferExchangeResult = exchangeClient.convertFromToWithPlaces(currency.name(), fromCurrency.name(), amount, places);
            return possibilityToTransferExchangeResult.getResult();
        }
        return amount;
    }

    private void validateBalance(final Account fromAccount, final float expectedMoneyTransfer) {
        final float balance = fromAccount.getBalance() - expectedMoneyTransfer;
        validateBalance(balance);
    }

    private void validateAmount(final float amount) {
        if (amount < 0) {
            throw new RuntimeException("You cant transfer invalid amount, it mus be greater than 0");
        }
    }

    private void validateBalance(final float balance) {
        if (balance < 0) {
            throw new RuntimeException("Balance after money transfer can not be under 0");
        }
    }

}
