# Money Transfer - Problem Statement

A Spring Boot application for performing money transfers between accounts with support for currency conversion, concurrency control, and configurable transaction fees.

This is demo application that was requested by JPMorgan Chase as a take-home Code Challenge

---

## Features

- Transfer funds between accounts in different currencies
- FX conversion rates in H2 Database
- Configurable transaction fee applied to the sender
- Concurrency-safe transfers using `@Transactional` and `@Version`
- Centralized error handling using `@ControllerAdvice`
- Comprehensive JUnit tests including concurrent scenarios
- Retry Mechanism for concurrent update failures

---

## Assumptions

1. Money can be transferred to an account only in its base currency.
2. Money can only be transferred from an account in its base currency
3. Since USD to AUD is the 'only' conversion rate provided, it should be configurable somehow.
4. Transaction Fee also change, it should be configurable somehow.

---

## Possible Enhancements

1. API Documentation / Javadocs
2. FX Rate Update with historical saving
3. Transactional history per account
4. Blocking of possible duplicate transfer like Gcash
5. Enhanced DTO for response body with transaction details
6. A transfer can be requested in the recipient’s currency, with the equivalent amount automatically converted from the sender’s base currency.
7. Enhance logging base on non personally identifiable information for better visibility.

---
## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database
- Maven
- JUnit 5
- Lombok

---

## How to Run
   ```bash
   ./mvnw spring-boot:run
   ```
OR
```
Run in Intellij via Maven Configuration
```

---

## API Endpoint

Request:
```
{
  "fromAccountNumber": "ACC001",
  "toAccountNumber": "ACC002",
  "amount": 50,
  "currency": "USD"
}
```
Response:
```
HTTP 200 OK
Transfer successful
```