package com.jpmorgan.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {
    @NotNull(message = "fromAccountNumber is required")
    private String fromAccountNumber;

    @NotNull(message = "toAccountNumber is required")
    private String toAccountNumber;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "currency is required")
    private String currency;
}
