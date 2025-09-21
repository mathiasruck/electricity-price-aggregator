package com.mathias.electricitypriceaggregator.infrastructure.csv;

import com.opencsv.bean.AbstractBeanField;
import org.springframework.util.StringUtils;

public class DoubleConverter extends AbstractBeanField<Double, String> {

    @Override
    protected Double convert(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            // Normalize decimal separator from comma to period
            String normalizedValue = value.replace(",", ".");
            return Double.parseDouble(normalizedValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format: " + value);
        }
    }
}