package com.mathias.electricitypriceaggregator.domain;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.domain.model.ElectricityPriceEstonia;

public class ElectricityPriceFactory {

    public static ElectricityPrice create(String country) {
        switch (country) {
            case "EE":
                return new ElectricityPriceEstonia();
            default:
                throw new IllegalArgumentException("Unsupported country code: " + country);
        }
    }
}
