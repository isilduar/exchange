package com.bakp.account;


import com.bakp.dto.AccountDto;
import com.bakp.dto.AccountTransferDto;
import com.bakp.exchange.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public AccountDto findAccount(@PathVariable long id) {
        return accountService.findAccount(id);
    }

    @PutMapping("/{currency}")
    public AccountDto createAccount(@PathVariable Currency currency) {
        return accountService.createAccount(currency);
    }

    @PutMapping("/add/{id}")
    public AccountDto addMoney(@PathVariable long id, @RequestParam float amount) {
        return accountService.addMoney(id, amount);
    }

    @PutMapping("/remove/{id}")
    public AccountDto removeMoney(@PathVariable long id, @RequestParam float amount) {
        return accountService.removeMoney(id, amount);
    }

    @PostMapping("/transfer/{from}/{to}")
    public AccountTransferDto transferMoney(@PathVariable long from, @PathVariable long to, @RequestParam float amount, @RequestParam Currency currency) {
        return accountService.transferMoney(from, to, amount, currency);
    }

    @DeleteMapping("/{id}")
    public void removeMoney(@PathVariable long id) {
        accountService.removeAccount(id);
    }
}
