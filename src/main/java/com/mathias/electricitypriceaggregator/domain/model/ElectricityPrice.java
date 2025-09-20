package com.mathias.electricitypriceaggregator.domain.model;

import com.opencsv.bean.CsvBindByName;

/**
 * Domain entity representing electricity price data
 */
public abstract class ElectricityPrice {

    @CsvBindByName(column = "Ajatempel (UTC)")
    private long recordedAt;

    public abstract String getCountry();

    public abstract Double getPrice();

    public abstract void setPrice(Double price);

    public long getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(long recordedAt) {
        this.recordedAt = recordedAt;
    }
}
