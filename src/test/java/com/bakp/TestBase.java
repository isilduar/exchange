package com.bakp;

import com.bakp.account.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManagerFactory;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ExchangeApplication.class, webEnvironment = RANDOM_PORT)
public abstract class TestBase {

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected EntityManagerFactory entityManagerFactory;

    @AfterEach
    public void tearDown() {
        accountRepository.deleteAll();
    }
}

