package com.transfer.test.repository;

import com.transfer.test.model.FXRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FXRateRepository extends JpaRepository<FXRate, Long> {
    Optional<FXRate> findByFromCurrencyAndToCurrency(String from, String to);
}
