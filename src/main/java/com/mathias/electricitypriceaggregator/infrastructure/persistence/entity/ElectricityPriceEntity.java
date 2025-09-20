package com.mathias.electricitypriceaggregator.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity for electricity price data
 */
@Entity
@Table(name = "electricity_prices",
       indexes = {
           @Index(name = "idx_timestamp", columnList = "timestamp"),
           @Index(name = "idx_date", columnList = "date")
       })
public class ElectricityPriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp", nullable = false, unique = true)
    private LocalDateTime timestamp;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "nps_estonia", nullable = false)
    private Double npsEstonia;

    public ElectricityPriceEntity() {
    }

    public ElectricityPriceEntity(LocalDateTime timestamp, Double npsEstonia) {
        this.timestamp = timestamp;
        this.date = timestamp.toLocalDate();
        this.npsEstonia = npsEstonia;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        if (timestamp != null) {
            this.date = timestamp.toLocalDate();
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
