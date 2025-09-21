package com.mathias.electricitypriceaggregator.domain.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.time.LocalDate;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Domain entity representing weather data
 */
public class WeatherData {

    private Long id;
    private LocalDate date;
    private Double averageTemperature; // Average temperature in Celsius

    public WeatherData() {
    }

    public WeatherData(LocalDate date, Double averageTemperature) {
        this.date = date;
        this.averageTemperature = averageTemperature;
    }

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
        WeatherData that = (WeatherData) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }
}
