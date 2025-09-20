package com.mathias.electricitypriceaggregator.domain.model;

import com.mathias.electricitypriceaggregator.infrastructure.csv.DoubleConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

import java.util.Objects;

/**
 * Domain entity representing electricity price data
 */
public class ElectricityPrice {

    @CsvBindByName(column = "Ajatempel (UTC)")
    private long timestamp;

    @CsvCustomBindByName(column = "NPS Eesti", converter = DoubleConverter.class)
    private Double npsEstonia;


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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
                ", timestamp=" + timestamp +
                ", npsEstonia=" + npsEstonia +
                '}';
    }
}
