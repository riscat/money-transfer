package com.transfer.test.service;


import com.transfer.test.model.Account;
import com.transfer.test.model.FXRate;
import com.transfer.test.repository.AccountRepository;
import com.transfer.test.repository.FXRateRepository;
import com.transfer.test.constant.ErrorConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({TransactionService.class, FXRateService.class})
public class TransactionServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FXRateRepository fxRateRepository;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        fxRateRepository.deleteAll();

        List<Account> accounts = accountRepository.saveAll(List.of(
                new Account("ACC001", "Alice", new BigDecimal("1000.00"), "USD"),
                new Account("ACC002", "Bob", new BigDecimal("500.00"), "JPN")
        ));

        fxRateRepository.saveAll(List.of(
                new FXRate("USD", "AUD", new BigDecimal("2.0")),
                new FXRate("AUD", "USD", new BigDecimal("0.5")),
                new FXRate("USD", "JPN", new BigDecimal("110")),
                new FXRate("JPN", "USD", new BigDecimal("0.009")),
                new FXRate("CNY", "USD", new BigDecimal("0.14")),
                new FXRate("USD", "CNY", new BigDecimal("7.1"))
        ));
    }

    @Test
    void testSingleUSDTransferFromAliceToBob() {
        transactionService.transfer("ACC001", "ACC002", new BigDecimal("50"), "USD");
        Account alice = accountRepository.findByAccountNumber("ACC001").orElseThrow();
        Account bob = accountRepository.findByAccountNumber("ACC002").orElseThrow();
        System.out.println("Alice: " + alice.getBalance());
        System.out.println("Bob: " + bob.getBalance());
    }

    @Test
    void testTwentyAUDTransfersFromBobToAlice() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {
                try {
                    transactionService.transfer("ACC002", "ACC001", new BigDecimal("20"), "AUD");
                    fail("Expected IllegalArgumentException not thrown");
                } catch (IllegalArgumentException e) {
                    if (!ErrorConstants.SOURCE_CURRENCY_MISMATCH.equals(e.getMessage())) {
                        fail("Unexpected exception message: " + e.getMessage());
                    }
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    void testConcurrentAUDTransferFromBobToAlice() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 20; i++) {
            executor.submit(() -> transactionService.transfer("ACC002", "ACC001", new BigDecimal("20"), "AUD"));
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Account bob = accountRepository.findByAccountNumber("ACC002").orElseThrow();
        Account alice = accountRepository.findByAccountNumber("ACC001").orElseThrow();
        System.out.println("Bob: " + bob.getBalance());
        System.out.println("Alice: " + alice.getBalance());
    }

    @Test
    void testValid40USDTransferFromAliceToBob() {
        transactionService.transfer("ACC001", "ACC002", new BigDecimal("40"), "USD");
    }

    @Test
    void testInvalid40CNYTransferFromAliceToBob() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.transfer("ACC001", "ACC002", new BigDecimal("40"), "CNY");
        });
        assertEquals("Transfer currency must match the source account's base currency", exception.getMessage());
    }

    @Test
    void testTransferUSDToJPN() {
        transactionService.transfer("ACC001", "ACC002", new BigDecimal("50"), "USD");

        Account alice = accountRepository.findByAccountNumber("ACC001").orElseThrow();
        Account bob = accountRepository.findByAccountNumber("ACC002").orElseThrow();

        assertEquals(new BigDecimal("949.50"), alice.getBalance());
        assertEquals(new BigDecimal("6000.00"), bob.getBalance());
    }

    @Test
    void testInsufficientBalance() {
        assertThrows(IllegalStateException.class, () ->
                transactionService.transfer("ACC001", "ACC002", new BigDecimal("10000"), "USD")
        );
    }

    @Test
    void testCurrencyMismatch() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer("ACC001", "ACC002", new BigDecimal("50"), "AUD")
        );
    }

    @Test
    void testFromAccountNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                transactionService.transfer("ACC005", "ACC002", new BigDecimal("50"), "AUD")
        );
    }

    @Test
    void testToAccountNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                transactionService.transfer("ACC001", "ACC005", new BigDecimal("50"), "AUD")
        );
    }
}
