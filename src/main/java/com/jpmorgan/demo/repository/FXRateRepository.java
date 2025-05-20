package com.jpmorgan.demo.repository;

import com.jpmorgan.demo.model.FXRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FXRateRepository extends JpaRepository<FXRate, Long> {
    Optional<FXRate> findByFromCurrencyAndToCurrency(String from, String to);
}
