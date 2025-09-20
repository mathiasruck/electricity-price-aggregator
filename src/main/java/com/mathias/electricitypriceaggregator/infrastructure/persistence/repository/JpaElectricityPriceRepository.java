package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.ElectricityPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for ElectricityPriceEntity
 */
@Repository
public interface JpaElectricityPriceRepository extends JpaRepository<ElectricityPriceEntity, Long> {

    //todo fix this, it should be instant not LocalDate. Remove?
    List<ElectricityPriceEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT e.timestamp FROM ElectricityPriceEntity e WHERE e.timestamp BETWEEN :startDate AND :endDate ORDER BY e.timestamp")
    List<LocalDate> findDistinctDatesBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}
