package com.mathias.electricitypriceaggregator.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

/**
 * JPA entity for electricity price data
 */
@Entity
@Table(name = "electricity_price",
        uniqueConstraints = @UniqueConstraint(name = "un_recorded_at_country", columnNames = {"recorded_at", "country"}),
        indexes = @Index(name = "idx_recorded_at_country", columnList = "recorded_at, country"))
public class ElectricityPriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recorded_at", columnDefinition = "TIMESTAMPTZ", nullable = false)
    private Instant recordedAt;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "country", nullable = false, length = 2)
    private String country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElectricityPriceEntity that = (ElectricityPriceEntity) o;
        return Objects.equals(recordedAt, that.recordedAt) &&
                Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
