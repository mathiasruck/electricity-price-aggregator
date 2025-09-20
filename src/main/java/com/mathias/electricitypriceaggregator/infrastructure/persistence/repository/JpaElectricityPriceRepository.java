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
    List<ElectricityPriceEntity> findByRecordedAtBetween(Instant startInstant, Instant endInstant);

    @Query("SELECT e.recordedAt FROM ElectricityPriceEntity e WHERE e.recordedAt BETWEEN :startInstant AND :endInstant ORDER BY e.recordedAt")
    List<LocalDate> findDistinctDatesBetween(@Param("startInstant") Instant startInstant, @Param("endInstant") Instant endInstant);
}
