package com.transfer.test.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @Column(name = "account_number", nullable = false, length = 64)
    private String accountNumber;
    private String name;
    private BigDecimal balance;
    private String currency;

    @Version
    private Long version;

    public Account(String accountNumber, String name, BigDecimal balance, String currency) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
        this.currency = currency;
    }
}
