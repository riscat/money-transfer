package com.transfer.test.service;

import com.transfer.test.model.FXRate;
import com.transfer.test.repository.FXRateRepository;
import com.transfer.test.constant.ErrorConstants;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
                .orElseThrow(() -> new IllegalArgumentException(ErrorConstants.FX_RATE_NOT_FOUND));
    }
}