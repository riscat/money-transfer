package com.jpmorgan.demo.config;

import com.jpmorgan.demo.model.Account;
import com.jpmorgan.demo.model.FXRate;
import com.jpmorgan.demo.repository.AccountRepository;
import com.jpmorgan.demo.repository.FXRateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(AccountRepository accountRepository, FXRateRepository fxRateRepository) {
        return args -> {
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
        };
    }
}