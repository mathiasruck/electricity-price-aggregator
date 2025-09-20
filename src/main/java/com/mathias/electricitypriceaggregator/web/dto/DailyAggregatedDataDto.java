package com.mathias.electricitypriceaggregator.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * DTO for API response containing aggregated daily data
 */
public class DailyAggregatedDataDto {

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("averageElectricityPrice")
    private Double averageElectricityPrice;

    @JsonProperty("averageTemperature")
    private Double averageTemperature;

    public DailyAggregatedDataDto() {
    }

    public DailyAggregatedDataDto(LocalDate date, Double averageElectricityPrice, Double averageTemperature) {
        this.date = date;
        this.averageElectricityPrice = averageElectricityPrice;
        this.averageTemperature = averageTemperature;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getAverageElectricityPrice() {
        return averageElectricityPrice;
    }

    public void setAverageElectricityPrice(Double averageElectricityPrice) {
        this.averageElectricityPrice = averageElectricityPrice;
    }

    public Double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(Double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }
}
