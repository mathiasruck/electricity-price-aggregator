package com.mathias.electricitypriceaggregator.infrastructure.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Client for fetching weather data from Open Meteo API
 */
@Component
public class WeatherApiClient {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "https://archive-api.open-meteo.com/v1/archive";
    private static final String LOCATION_TALLINN = "latitude=59.4370&longitude=24.7536"; // Tallinn, Estonia coordinates

    public WeatherApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetch daily average temperature for a specific date
     */
    public Double fetchDailyAverageTemperature(LocalDate date) {
        try {
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String url = String.format("%s?%s&start_date=%s&end_date=%s&hourly=temperature_2m",
                    BASE_URL, LOCATION_TALLINN, dateStr, dateStr);

            WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);

            if (response != null && response.getHourly() != null &&
                response.getHourly().getTemperature2m() != null &&
                !response.getHourly().getTemperature2m().isEmpty()) {

                // Calculate average temperature for the day
                List<Double> temperatures = response.getHourly().getTemperature2m();
                return temperatures.stream()
                        .filter(temp -> temp != null)
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error fetching weather data for date " + date + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Response model for Open Meteo API
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherApiResponse {

        @JsonProperty("hourly")
        private HourlyData hourly;

        public HourlyData getHourly() {
            return hourly;
        }

        public void setHourly(HourlyData hourly) {
            this.hourly = hourly;
        }
    }

    /**
     * Hourly data model for Open Meteo API response
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HourlyData {

        @JsonProperty("temperature_2m")
        private List<Double> temperature2m;

        public List<Double> getTemperature2m() {
            return temperature2m;
        }

        public void setTemperature2m(List<Double> temperature2m) {
            this.temperature2m = temperature2m;
        }
    }
}
