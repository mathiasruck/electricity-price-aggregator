package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.ElectricityPriceEntity;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper.ElectricityPriceMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public ElectricityPrice save(ElectricityPrice electricityPrice) {
        ElectricityPriceEntity entity = mapper.toEntity(electricityPrice);
        ElectricityPriceEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ElectricityPrice> findByTimestamp(LocalDateTime timestamp) {
        return jpaRepository.findByTimestamp(timestamp)
                .map(mapper::toDomain);
    }

    @Override
    public List<ElectricityPrice> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByDateBetween(startDate, endDate).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<LocalDate> findDistinctDatesBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findDistinctDatesBetween(startDate, endDate);
    }

    @Override
    public List<ElectricityPrice> saveAll(List<ElectricityPrice> electricityPrices) {
        List<ElectricityPriceEntity> entities = electricityPrices.stream()
                .map(mapper::toEntity)
                .toList();
        List<ElectricityPriceEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
