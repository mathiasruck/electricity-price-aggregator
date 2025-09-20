package com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper;

import com.mathias.electricitypriceaggregator.domain.model.WeatherData;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.WeatherDataEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between WeatherData domain model and JPA entity
 */
@Component
public class WeatherDataMapper {

    public WeatherDataEntity toEntity(WeatherData domain) {
        if (domain == null) {
            return null;
        }

        WeatherDataEntity entity = new WeatherDataEntity();
        entity.setId(domain.getId());
        entity.setDate(domain.getDate());
        entity.setAverageTemperature(domain.getAverageTemperature());
        return entity;
    }

    public WeatherData toDomain(WeatherDataEntity entity) {
        if (entity == null) {
            return null;
        }

        WeatherData domain = new WeatherData();
        domain.setId(entity.getId());
        domain.setDate(entity.getDate());
        domain.setAverageTemperature(entity.getAverageTemperature());
        return domain;
    }
}
