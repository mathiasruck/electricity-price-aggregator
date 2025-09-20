package com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.ElectricityPriceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.mathias.electricitypriceaggregator.domain.ElectricityPriceFactory.create;
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
        entity.setRecordedAt(ofEpochSecond(domain.getRecordedAt()));
        entity.setPrice(domain.getPrice());
        return entity;
    }

    public ElectricityPrice toDomain(ElectricityPriceEntity entity) {
        if (entity == null) {
            return null;
        }

        ElectricityPrice domain = create(entity.getCountry());
        domain.setRecordedAt(entity.getRecordedAt().getEpochSecond());
        domain.setPrice(entity.getPrice());
        return domain;
    }

    public List<ElectricityPriceEntity> toEntity(List<ElectricityPrice> domain) {
        return domain.stream()
                .map(this::toEntity)
                .toList();
    }
}
