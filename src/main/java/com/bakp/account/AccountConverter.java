package com.bakp.account;

import com.bakp.dto.AccountDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountConverter implements Converter<Account, AccountDto> {

    @Override
    public AccountDto convert(final Account account) {
        if (null != account) {
            return AccountDto.builder()
                    .id(account.getId())
                    .currency(account.getCurrency())
                    .balance(account.getBalance())
                    .build();
        }
        return null;
    }
}
