package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper.ElectricityPriceMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static java.time.ZoneOffset.UTC;

/**
 * Implementation of ElectricityPriceRepository using JPA
 */
@Component
public class ElectricityPriceRepositoryImpl implements ElectricityPriceRepository {

    private final JpaElectricityPriceRepository jpaRepository;
    private final ElectricityPriceMapper mapper;

    public ElectricityPriceRepositoryImpl(JpaElectricityPriceRepository jpaRepository,
                                          ElectricityPriceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ElectricityPrice> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        Instant startInstant = getBeginningOfTheDay(startDate);
        Instant endInstant = getEndOfTheDay(endDate);
        return jpaRepository.findByRecordedAtBetween(startInstant, endInstant)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<LocalDate> findPricesDateWithoutWeather() {
        return jpaRepository.findPricesDateWithoutWeather()
                .stream()
                .map(Date::toLocalDate)
                .toList();
    }

    @Override
    public List<Integer> findRecordedHoursByDate(LocalDate date) {
        return jpaRepository.findRecordedHoursByDate(date);
    }

    private static Instant getEndOfTheDay(LocalDate endDate) {
        return endDate
                .plusDays(1)
                .atStartOfDay(UTC)
                .minusNanos(1)
                .toInstant();
    }

    private static Instant getBeginningOfTheDay(LocalDate startDate) {
        return startDate
                .atStartOfDay()
                .toInstant(UTC);
    }
}
