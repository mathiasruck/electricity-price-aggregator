package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.domain.model.WeatherData;
import com.mathias.electricitypriceaggregator.domain.repository.WeatherDataRepository;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.WeatherDataEntity;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper.WeatherDataMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of WeatherDataRepository using JPA
 */
@Component
public class WeatherDataRepositoryImpl implements WeatherDataRepository {

    private final JpaWeatherDataRepository jpaRepository;
    private final WeatherDataMapper mapper;

    public WeatherDataRepositoryImpl(JpaWeatherDataRepository jpaRepository,
                                     WeatherDataMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public WeatherData save(WeatherData weatherData) {
        WeatherDataEntity entity = mapper.toEntity(weatherData);
        WeatherDataEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<WeatherData> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByDateBetween(startDate, endDate).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
