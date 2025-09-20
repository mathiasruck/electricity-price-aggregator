package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.ElectricityPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for ElectricityPriceEntity
 */
@Repository
public interface JpaElectricityPriceRepository extends JpaRepository<ElectricityPriceEntity, Long> {

    Optional<ElectricityPriceEntity> findByTimestamp(LocalDateTime timestamp);

    List<ElectricityPriceEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT DISTINCT e.date FROM ElectricityPriceEntity e WHERE e.date BETWEEN :startDate AND :endDate ORDER BY e.date")
    List<LocalDate> findDistinctDatesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
