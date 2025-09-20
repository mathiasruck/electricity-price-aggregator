package com.mathias.electricitypriceaggregator.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Value object representing aggregated daily data
 */
public class DailyAggregatedData {

    private final LocalDate date;
    private final Double averageElectricityPrice;
    private final Double averageTemperature;

    public DailyAggregatedData(LocalDate date, Double averageElectricityPrice, Double averageTemperature) {
        this.date = date;
        this.averageElectricityPrice = averageElectricityPrice;
        this.averageTemperature = averageTemperature;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getAverageElectricityPrice() {
        return round(averageElectricityPrice);
    }

    public Double getAverageTemperature() {
        return round(averageTemperature);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyAggregatedData that = (DailyAggregatedData) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(averageElectricityPrice, that.averageElectricityPrice) &&
                Objects.equals(averageTemperature, that.averageTemperature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, averageElectricityPrice, averageTemperature);
    }

    @Override
    public String toString() {
        return "DailyAggregatedData{" +
                "date=" + date +
                ", averageElectricityPrice=" + averageElectricityPrice +
                ", averageTemperature=" + averageTemperature +
                '}';
    }

    private Double round(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value)
                .setScale(1, RoundingMode.HALF_UP) // round
                .doubleValue();
    }
}
