package com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.ElectricityPriceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.time.Instant.ofEpochSecond;

/**
 * Mapper for converting between ElectricityPrice domain model and JPA entity
 */
@Component
public class ElectricityPriceMapper {

    public ElectricityPriceEntity toEntity(ElectricityPrice domain) {
        if (domain == null) {
            return null;
        }

        ElectricityPriceEntity entity = new ElectricityPriceEntity();
        entity.setTimestamp(ofEpochSecond(domain.getTimestamp()));
        entity.setNpsEstonia(domain.getNpsEstonia());
        return entity;
    }

    public ElectricityPrice toDomain(ElectricityPriceEntity entity) {
        if (entity == null) {
            return null;
        }

        ElectricityPrice domain = new ElectricityPrice();
        domain.setTimestamp(entity.getTimestamp().getEpochSecond());
        domain.setNpsEstonia(entity.getNpsEstonia());
        return domain;
    }

    public List<ElectricityPriceEntity> toEntity(List<ElectricityPrice> domain) {
        return domain.stream()
                .map(this::toEntity)
                .toList();
    }
}
