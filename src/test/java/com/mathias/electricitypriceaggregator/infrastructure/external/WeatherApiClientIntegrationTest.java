package com.mathias.electricitypriceaggregator.infrastructure.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class WeatherApiClientIntegrationTest {

    @Mock
    private RestTemplate restTemplate;

    private WeatherApiClient weatherApiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherApiClient = new WeatherApiClient(restTemplate);
    }

    @Test
    void fetchDailyAverageTemperature_WithValidDate_ReturnsTemperature() {
        // Arrange
        LocalDate testDate = LocalDate.now().minusDays(1);
        List<Integer> availableHours = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

        WeatherApiClient.WeatherApiResponse mockResponse = new WeatherApiClient.WeatherApiResponse();
        WeatherApiClient.HourlyData hourlyData = new WeatherApiClient.HourlyData();
        hourlyData.setTemperature2m(Arrays.asList(20.0, 21.0, 22.0, 23.0, 24.0, 25.0,
                26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0, 33.0, 34.0, 35.0,
                36.0, 37.0, 38.0, 39.0, 40.0, 41.0, 42.0, 43.0));
        mockResponse.setHourly(hourlyData);

        when(restTemplate.getForObject(any(String.class), eq(WeatherApiClient.WeatherApiResponse.class)))
                .thenReturn(mockResponse);

        // Act
        Double result = weatherApiClient.fetchDailyAverageTemperature(testDate, availableHours);

        // Assert
        assertNotNull(result);
        assertEquals(25.5, result, 0.01); // Average of first 12 hours (20.0 to 31.0)
    }

    @Test
    void fetchDailyAverageTemperature_WithFutureDate_ReturnsNull() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusDays(1);
        List<Integer> availableHours = Arrays.asList(0, 1, 2, 3);

        when(restTemplate.getForObject(any(String.class), eq(WeatherApiClient.WeatherApiResponse.class)))
                .thenReturn(null);

        // Act
        Double result = weatherApiClient.fetchDailyAverageTemperature(futureDate, availableHours);

        // Assert
        assertNull(result, "Future dates should return null as historical data is not available");
    }

    @Test
    void fetchDailyAverageTemperature_WithEmptyHoursList_ReturnsNull() {
        // Arrange
        LocalDate testDate = LocalDate.now().minusDays(1);
        List<Integer> emptyHours = List.of();

        // Act
        Double result = weatherApiClient.fetchDailyAverageTemperature(testDate, emptyHours);

        // Assert
        assertNull(result, "Empty hours list should return null");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidHours")
    void fetchDailyAverageTemperature_WithInvalidHourIndices_HandlesGracefully(List<Integer> invalidHours) {
        // Arrange
        LocalDate testDate = LocalDate.now().minusDays(1);

        WeatherApiClient.WeatherApiResponse mockResponse = new WeatherApiClient.WeatherApiResponse();
        WeatherApiClient.HourlyData hourlyData = new WeatherApiClient.HourlyData();
        hourlyData.setTemperature2m(Arrays.asList(20.0, 21.0, 22.0, 23.0));
        mockResponse.setHourly(hourlyData);

        when(restTemplate.getForObject(any(String.class), eq(WeatherApiClient.WeatherApiResponse.class)))
                .thenReturn(mockResponse);

        // Act
        Double result = weatherApiClient.fetchDailyAverageTemperature(testDate, invalidHours);

        // Assert
        assertNull(result, "Invalid hour indices should be handled gracefully");
    }

    private static Stream<Arguments> provideInvalidHours() {
        return Stream.of(
                Arguments.of(Arrays.asList(-1, 24, 25)),
                Arguments.of(Arrays.asList(24)),
                Arguments.of(Arrays.asList(-1)),
                Arguments.of(Arrays.asList(100))
        );
    }

    @Test
    void fetchDailyAverageTemperature_WithNullInput_HandlesGracefully() {
        // Act & Assert
        assertNull(weatherApiClient.fetchDailyAverageTemperature(null, Arrays.asList(0, 1)));
        assertNull(weatherApiClient.fetchDailyAverageTemperature(LocalDate.now(), null));
        assertNull(weatherApiClient.fetchDailyAverageTemperature(null, null));
    }

    @Test
    void fetchDailyAverageTemperature_WhenApiThrowsException_ReturnsNull() {
        // Arrange
        LocalDate testDate = LocalDate.now().minusDays(1);
        List<Integer> availableHours = Arrays.asList(0, 1, 2, 3);

        when(restTemplate.getForObject(any(String.class), eq(WeatherApiClient.WeatherApiResponse.class)))
                .thenThrow(new RuntimeException("API Error"));

        // Act
        Double result = weatherApiClient.fetchDailyAverageTemperature(testDate, availableHours);

        // Assert
        assertNull(result, "Should return null when API throws exception");
    }
}
