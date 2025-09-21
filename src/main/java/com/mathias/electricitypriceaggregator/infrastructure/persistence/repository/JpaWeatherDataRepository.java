package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.WeatherDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for WeatherDataEntity
 */
@Repository
public interface JpaWeatherDataRepository extends JpaRepository<WeatherDataEntity, Long> {

    List<WeatherDataEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
