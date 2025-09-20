package com.mathias.electricitypriceaggregator.domain.repository;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for ElectricityPrice entities
 */
public interface ElectricityPriceRepository {

    ElectricityPrice save(ElectricityPrice electricityPrice);

    Optional<ElectricityPrice> findByTimestamp(LocalDateTime timestamp);

    List<ElectricityPrice> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<LocalDate> findDistinctDatesBetween(LocalDate startDate, LocalDate endDate);

    List<ElectricityPrice> saveAll(List<ElectricityPrice> electricityPrices);
}
