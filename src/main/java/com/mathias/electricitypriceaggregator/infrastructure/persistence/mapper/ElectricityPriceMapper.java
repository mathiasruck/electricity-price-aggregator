package com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.ElectricityPriceEntity;
import org.springframework.stereotype.Component;

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
        entity.setId(domain.getId());
        entity.setTimestamp(domain.getTimestamp());
        entity.setDate(domain.getDate());
        entity.setNpsEstonia(domain.getNpsEstonia());
        return entity;
    }

    public ElectricityPrice toDomain(ElectricityPriceEntity entity) {
        if (entity == null) {
            return null;
        }

        ElectricityPrice domain = new ElectricityPrice();
        domain.setId(entity.getId());
        domain.setTimestamp(entity.getTimestamp());
        domain.setDate(entity.getDate());
        domain.setNpsEstonia(entity.getNpsEstonia());
        return domain;
    }
}
