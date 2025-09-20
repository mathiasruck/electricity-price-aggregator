package com.mathias.electricitypriceaggregator.domain.repository;

import com.mathias.electricitypriceaggregator.domain.model.WeatherData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for WeatherData entities
 */
public interface WeatherDataRepository {

    WeatherData save(WeatherData weatherData);

    Optional<WeatherData> findByDate(LocalDate date);

    List<WeatherData> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<WeatherData> saveAll(List<WeatherData> weatherDataList);
}
