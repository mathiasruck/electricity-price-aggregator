package com.mathias.electricitypriceaggregator.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * JPA entity for weather data
 */
@Entity
@Table(name = "weather_data",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_weather_data_date", columnNames = "date")
        })
public class WeatherDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "average_temperature", nullable = false)
    private Double averageTemperature;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(Double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherDataEntity that = (WeatherDataEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
