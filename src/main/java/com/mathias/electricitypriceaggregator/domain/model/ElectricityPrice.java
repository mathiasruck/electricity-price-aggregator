package com.mathias.electricitypriceaggregator.domain.model;

import com.opencsv.bean.CsvBindByName;

/**
 * Domain entity representing electricity price data
 */
public abstract class ElectricityPrice {

    @CsvBindByName(column = "Ajatempel (UTC)")
    private Long recordedAt;

    public abstract String getCountry();

    public abstract Double getPrice();

    public abstract void setPrice(Double price);

    public Long getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Long recordedAt) {
        this.recordedAt = recordedAt;
    }
}
