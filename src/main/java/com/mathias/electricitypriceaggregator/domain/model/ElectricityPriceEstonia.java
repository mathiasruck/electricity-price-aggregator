package com.mathias.electricitypriceaggregator.domain.model;

import com.mathias.electricitypriceaggregator.infrastructure.csv.DoubleConverter;
import com.opencsv.bean.CsvCustomBindByName;

import java.util.Objects;

/**
 * Domain entity representing electricity price data
 */
public class ElectricityPriceEstonia extends ElectricityPrice {

    private static final String COUNTRY = "EE";

    @CsvCustomBindByName(column = "NPS Eesti", converter = DoubleConverter.class)
    private Double price;

    @Override
    public Double getPrice() {
        return price;
    }

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
        return Objects.equals(getRecordedAt(), that.getRecordedAt())
                && Objects.equals(getCountry(), that.getCountry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRecordedAt());
    }

    @Override
    public String toString() {
        return "ElectricityPrice{" +
                ", recordedAt=" + getRecordedAt() +
                ", price=" + price +
                '}';
    }
}
