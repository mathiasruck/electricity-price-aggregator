package com.mathias.electricitypriceaggregator.infrastructure.csv;

import com.opencsv.bean.AbstractBeanField;

import static java.lang.Double.parseDouble;

public class DoubleConverter extends AbstractBeanField<Double, String> {

    @Override
    protected Double convert(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            String normalized = value.replace(",", ".");
            return parseDouble(normalized);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format: " + value);
        }
    }
}