package com.mathias.electricitypriceaggregator.domain.repository;

import com.mathias.electricitypriceaggregator.domain.model.WeatherData;

import java.time.LocalDate;
import java.util.List;

/**
 * Domain repository interface for WeatherData entities
 */
public interface WeatherDataRepository {

    WeatherData save(WeatherData weatherData);

    List<WeatherData> findByDateBetween(LocalDate startDate, LocalDate endDate);

}
