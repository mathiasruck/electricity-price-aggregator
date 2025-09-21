package com.mathias.electricitypriceaggregator.infrastructure.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Client for fetching weather data from Open Meteo API
 */
@Component
public class WeatherApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherApiClient.class);
    private static final String BASE_URL = "https://archive-api.open-meteo.com/v1/archive";
    private static final String LOCATION_ESTONIA = "latitude=59&longitude=26";
    private static final String TIMEZONE = "UTC";

    private final RestTemplate restTemplate;

    public WeatherApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetch daily average temperature for a specific date, considering only hours with available electricity price data
     *
     * @param date                 The date to fetch data for
     * @param availableHourIndices List of hour indices (0-23) for which electricity price data is available
     * @return The average temperature for the specified hours, or null if no data
     */
    public Double fetchDailyAverageTemperature(LocalDate date, List<Integer> availableHourIndices) {
        try {
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String url = String.format("%s?%s&start_date=%s&end_date=%s&hourly=temperature_2m&timezone=%s",
                    BASE_URL, LOCATION_ESTONIA, dateStr, dateStr, TIMEZONE);
            LOG.debug("Fetching weather data from URL: {}", url);
            WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);

            if (response != null && response.getHourly() != null &&
                    response.getHourly().getTemperature2m() != null &&
                    !response.getHourly().getTemperature2m().isEmpty()) {

                List<Double> temperatures = response.getHourly().getTemperature2m();
                // Filter temperatures to only those hours with available price data
                List<Double> filteredTemperatures = availableHourIndices.stream()
                        .filter(hourIdx -> hourIdx >= 0 && hourIdx < temperatures.size())
                        .map(temperatures::get)
                        .filter(Objects::nonNull)
                        .toList();
                if (filteredTemperatures.isEmpty()) {
                    return null;
                }
                return filteredTemperatures.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);
            }

            return null;
        } catch (Exception e) {
            LOG.error("Error fetching weather data for date {}: {}", date, e.getMessage());
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
