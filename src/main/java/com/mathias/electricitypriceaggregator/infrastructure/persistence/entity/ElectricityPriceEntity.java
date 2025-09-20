package com.mathias.electricitypriceaggregator.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

/**
 * JPA entity for electricity price data
 */
@Entity
@Table(name = "electricity_prices",
        uniqueConstraints = @UniqueConstraint(name = "un_timestamp", columnNames = "timestamp"),
        indexes = @Index(name = "idx_timestamp", columnList = "timestamp"))
public class ElectricityPriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMPTZ", nullable = false, unique = true)
    private Instant timestamp;

    @Column(name = "nps_estonia", nullable = false)
    private Double npsEstonia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Double getNpsEstonia() {
        return npsEstonia;
    }

    public void setNpsEstonia(Double npsEstonia) {
        this.npsEstonia = npsEstonia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElectricityPriceEntity that = (ElectricityPriceEntity) o;
        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }
}
