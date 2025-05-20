package com.jpmorgan.demo.service;

import com.jpmorgan.demo.model.Account;
import com.jpmorgan.demo.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static com.jpmorgan.demo.constant.ErrorConstants.*;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final FXRateService fxService;
    @Value("${transaction.fee.rate:0.01}")
    private BigDecimal transactionFee;

    @Value("${transaction.retry.max:3}")
    private int maxRetries;

    public TransactionService(AccountRepository accountRepository, FXRateService fxService) {
        this.accountRepository = accountRepository;
        this.fxService = fxService;
    }

    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount, String currency) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                executeTransfer(fromAccountNumber, toAccountNumber, amount, currency);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                System.out.println("[Retry " + attempt + "] Optimistic locking failure: " + e.getMessage());
                if (attempt == maxRetries) {
                    throw new IllegalStateException(MAX_RETRY_FAILED);
                }
            }
        }
    }

    private void executeTransfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount, String currency) {
        Account from = getAccountOrThrow(fromAccountNumber, FROM_ACCOUNT_NOT_FOUND);
        Account to = getAccountOrThrow(toAccountNumber, TO_ACCOUNT_NOT_FOUND);

        validateCurrencyMatch(from, currency);
        validateSufficientBalance(from, amount);

        BigDecimal fxRate = fxService.getRate(currency, to.getCurrency());
        BigDecimal fee = calculateFee(amount);
        BigDecimal totalDeduct = amount.add(fee);
        BigDecimal convertedAmount = amount.multiply(fxRate);

        updateBalances(from, to, totalDeduct, convertedAmount);
    }

    private Account getAccountOrThrow(String accountNumber, String errorMessage) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NoSuchElementException(errorMessage));
    }

    private void validateCurrencyMatch(Account from, String currency) {
        if (!currency.equals(from.getCurrency())) {
            throw new IllegalArgumentException(SOURCE_CURRENCY_MISMATCH);
        }
    }

    private void validateSufficientBalance(Account from, BigDecimal amount) {
        BigDecimal totalDeduct = amount.add(calculateFee(amount));
        if (from.getBalance().compareTo(totalDeduct) < 0) {
            throw new IllegalStateException(INSUFFICIENT_BALANCE);
        }
    }

    private BigDecimal calculateFee(BigDecimal amount) {
        return amount.multiply(transactionFee);
    }

    private void updateBalances(Account from, Account to, BigDecimal totalDeduct, BigDecimal convertedAmount) {
        from.setBalance(from.getBalance().subtract(totalDeduct));
        to.setBalance(to.getBalance().add(convertedAmount));
        accountRepository.save(from);
        accountRepository.save(to);
    }
}
