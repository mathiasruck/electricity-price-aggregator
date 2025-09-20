package com.mathias.electricitypriceaggregator.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing electricity price data
 */
public class ElectricityPrice {

    private Long id;
    private LocalDateTime timestamp;
    private LocalDate date;
    private Double npsEstonia; // NPS Eesti price in EUR/MWh

    public ElectricityPrice() {
    }

    public ElectricityPrice(LocalDateTime timestamp, Double npsEstonia) {
        this.timestamp = timestamp;
        this.date = timestamp.toLocalDate();
        this.npsEstonia = npsEstonia;
    }

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
        ElectricityPrice that = (ElectricityPrice) o;
        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return "ElectricityPrice{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", date=" + date +
                ", npsEstonia=" + npsEstonia +
                '}';
    }
}
