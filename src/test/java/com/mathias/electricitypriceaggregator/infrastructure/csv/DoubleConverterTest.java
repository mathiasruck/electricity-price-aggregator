package com.mathias.electricitypriceaggregator.infrastructure.csv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DoubleConverterTest {

    @Test
    void convert_withValidDecimalPoint_returnsDouble() {
        Double result = new DoubleConverter().convert("123.45");

        assertEquals(123.45, result);
    }

    @Test
    void convert_withValidDecimalComma_returnsDouble() {
        Double result = new DoubleConverter().convert("123,45");

        assertEquals(123.45, result);
    }

    @Test
    void convert_withNegativeNumber_returnsNegativeDouble() {
        assertEquals(-123.45, new DoubleConverter().convert("-123.45"));
        assertEquals(-123.45, new DoubleConverter().convert("-123,45"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void convert_withEmptyOrWhitespace_returnsNull(String input) {
        Double result = new DoubleConverter().convert(input);

        assertNull(result);
    }

    @Test
    void convert_withNull_returnsNull() {
        Double result = new DoubleConverter().convert(null);

        assertNull(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abc",
            "12.34.56",
            "12,34,56",
            "12.34,56",
            "12,34.",
            "12a34"
    })
    void convert_withInvalidFormat_throwsException(String input) {
        Exception exception = assertThrows(
                RuntimeException.class,
                () -> new DoubleConverter().convert(input)
        );
        assertTrue(exception.getMessage().contains("Invalid number format"));
    }

    @Test
    void convert_withLargeNumber_handlesCorrectly() {
        Double result = new DoubleConverter().convert("1234567890.123456");

        assertEquals(1234567890.123456, result);
    }

    @Test
    void convert_withScientificNotation_handlesCorrectly() {
        Double result = new DoubleConverter().convert("1.23456E-10");

        assertEquals(1.23456E-10, result);
    }
}
