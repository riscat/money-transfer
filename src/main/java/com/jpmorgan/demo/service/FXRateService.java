package com.jpmorgan.demo.service;

import com.jpmorgan.demo.model.FXRate;
import com.jpmorgan.demo.repository.FXRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.jpmorgan.demo.constant.ErrorConstants.FX_RATE_NOT_FOUND;

@Service
public class FXRateService {
    private final FXRateRepository fxRateRepository;

    public FXRateService(FXRateRepository fxRateRepository) {
        this.fxRateRepository = fxRateRepository;
    }

    public BigDecimal getRate(String from, String to) {
        if (from.equals(to)) return BigDecimal.ONE;
        return fxRateRepository.findByFromCurrencyAndToCurrency(from, to)
                .map(FXRate::getRate)
                .orElseThrow(() -> new IllegalArgumentException(FX_RATE_NOT_FOUND));
    }
}