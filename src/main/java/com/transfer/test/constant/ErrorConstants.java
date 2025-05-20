package com.transfer.test.constant;

public class ErrorConstants {
    public static final String SOURCE_CURRENCY_MISMATCH = "Transfer currency must match the source account's base currency";
    public static final String FX_RATE_NOT_FOUND = "FX rate not found for currency pair";
    public static final String INSUFFICIENT_BALANCE = "Insufficient balance";
    public static final String FROM_ACCOUNT_NOT_FOUND = "Source of Fund not found";
    public static final String TO_ACCOUNT_NOT_FOUND = "Destination of Fund not found";
    public static final String MAX_RETRY_FAILED = "Failed after max retry attempts due to concurrent update";
}