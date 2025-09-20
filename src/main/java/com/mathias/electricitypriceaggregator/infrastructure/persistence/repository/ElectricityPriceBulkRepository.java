package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.ElectricityPriceEntity;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper.ElectricityPriceMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class ElectricityPriceBulkRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ElectricityPriceMapper mapper;

    public ElectricityPriceBulkRepository(JdbcTemplate jdbcTemplate, ElectricityPriceMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Transactional
    public void upsertAll(List<ElectricityPrice> prices) {
        List<ElectricityPriceEntity> entities = mapper.toEntity(prices);
        String sql = """
                INSERT INTO electricity_price (recorded_at, price, country)
                VALUES (?, ?, ?)
                ON CONFLICT (recorded_at, country)
                DO UPDATE SET price = EXCLUDED.price
                """;

        jdbcTemplate.batchUpdate(sql, entities, 1000,
                (ps, entity) -> {
                    ps.setObject(1, Timestamp.from(entity.getRecordedAt()));
                    ps.setDouble(2, entity.getPrice());
                    ps.setString(3, entity.getCountry());
                });
    }
}