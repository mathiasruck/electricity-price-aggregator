package com.mathias.electricitypriceaggregator.domain.valueobject;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Value object representing aggregated daily data
 */
public record DailyAggregatedData(LocalDate date, Double averageElectricityPrice, Double averageTemperature) {

    @Override
    public Double averageElectricityPrice() {
        return round(averageElectricityPrice);
    }

    @Override
    public Double averageTemperature() {
        return round(averageTemperature);
    }

    private Double round(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }


}
