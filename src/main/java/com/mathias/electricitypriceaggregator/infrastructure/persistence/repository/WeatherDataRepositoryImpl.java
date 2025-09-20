package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.domain.model.WeatherData;
import com.mathias.electricitypriceaggregator.domain.repository.WeatherDataRepository;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.WeatherDataEntity;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper.WeatherDataMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    public Optional<WeatherData> findByDate(LocalDate date) {
        return jpaRepository.findByDate(date)
                .map(mapper::toDomain);
    }

    @Override
    public List<WeatherData> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByDateBetween(startDate, endDate).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WeatherData> saveAll(List<WeatherData> weatherDataList) {
        List<WeatherDataEntity> entities = weatherDataList.stream()
                .map(mapper::toEntity)
                .toList();
        List<WeatherDataEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
