package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper.ElectricityPriceMapper;
import org.springframework.stereotype.Component;

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
        return jpaRepository.findByDateBetween(startDate, endDate).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<LocalDate> findDistinctDatesBetween(LocalDate startDate, LocalDate endDate) {
        Instant startInstant = startDate
                .atStartOfDay()
                .toInstant(UTC);
        Instant endInstant = endDate
                .plusDays(1)
                .atStartOfDay(UTC)
                .minusNanos(1)
                .toInstant();
        return jpaRepository.findDistinctDatesBetween(startInstant, endInstant);
    }
}
