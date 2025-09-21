package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.ElectricityPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for ElectricityPriceEntity
 */
@Repository
public interface JpaElectricityPriceRepository extends JpaRepository<ElectricityPriceEntity, Long> {

    @Query("SELECT e FROM ElectricityPriceEntity e WHERE e.recordedAt >= :startInstant AND e.recordedAt < :endInstant ORDER BY e.recordedAt")
    List<ElectricityPriceEntity> findByRecordedAtBetween(Instant startInstant, Instant endInstant);

    @Query("""
            SELECT DISTINCT FUNCTION('date', e.recordedAt)
                FROM ElectricityPriceEntity e
                WHERE FUNCTION('DATE', e.recordedAt) NOT IN (
                    SELECT w.date FROM WeatherDataEntity w
                )
            """)
    List<Date> findPricesDateWithoutWeather();
}
