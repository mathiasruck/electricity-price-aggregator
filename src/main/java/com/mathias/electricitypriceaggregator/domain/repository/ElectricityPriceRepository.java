package com.mathias.electricitypriceaggregator.domain.repository;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;

import java.time.LocalDate;
import java.util.List;

/**
 * Domain repository interface for ElectricityPrice entities
 */
public interface ElectricityPriceRepository {

    List<ElectricityPrice> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<LocalDate> findPricesDateWithoutWeather();
}
