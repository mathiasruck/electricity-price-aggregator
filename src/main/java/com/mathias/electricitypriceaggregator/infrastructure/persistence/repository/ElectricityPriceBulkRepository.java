package com.mathias.electricitypriceaggregator.infrastructure.persistence.repository;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.ElectricityPriceEntity;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.mapper.ElectricityPriceMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                INSERT INTO electricity_price (timestamp, nps_estonia)
                VALUES (?, ?)
                ON CONFLICT (timestamp)
                DO UPDATE SET nps_estonia = EXCLUDED.nps_estonia
                """;

        jdbcTemplate.batchUpdate(sql, entities, 1000,
                (ps, entity) -> {
                    ps.setObject(1, entity.getTimestamp());
                    ps.setDouble(2, entity.getNpsEstonia());
                });
    }
}