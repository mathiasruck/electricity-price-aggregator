package com.mathias.electricitypriceaggregator.domain.model;

import com.mathias.electricitypriceaggregator.infrastructure.csv.DoubleConverter;
import com.opencsv.bean.CsvCustomBindByName;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Domain entity representing electricity price data for Estonia
 */
public class ElectricityPriceEstonia extends ElectricityPrice {

    private static final String COUNTRY = "EE";

    @CsvCustomBindByName(column = "NPS Eesti", converter = DoubleConverter.class, required = true)
    private Double price;

    @Override
    public Double getPrice() {
        return price;
    }

    @Override
    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String getCountry() {
        return COUNTRY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElectricityPriceEstonia that = (ElectricityPriceEstonia) o;
        return Objects.equals(getRecordedAt(), that.getRecordedAt()) &&
                Objects.equals(getCountry(), that.getCountry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRecordedAt(), getCountry());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }
}
