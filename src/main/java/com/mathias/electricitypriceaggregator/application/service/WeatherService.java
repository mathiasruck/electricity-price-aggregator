package com.mathias.electricitypriceaggregator.application.service;

import com.mathias.electricitypriceaggregator.domain.model.WeatherData;
import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import com.mathias.electricitypriceaggregator.domain.repository.WeatherDataRepository;
import com.mathias.electricitypriceaggregator.infrastructure.external.WeatherApiClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Application service for handling weather data operations
 */
@Service
@Transactional
public class WeatherService {

    private final WeatherDataRepository weatherDataRepository;
    private final ElectricityPriceRepository electricityPriceRepository;
    private final WeatherApiClient weatherApiClient;

    public WeatherService(WeatherDataRepository weatherDataRepository,
                          ElectricityPriceRepository electricityPriceRepository,
                          WeatherApiClient weatherApiClient) {
        this.weatherDataRepository = weatherDataRepository;
        this.electricityPriceRepository = electricityPriceRepository;
        this.weatherApiClient = weatherApiClient;
    }

    /**
     * Scheduled method to fetch weather data every minute
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    // todo fix this sync, to get only the missing weather data for the dates that have electricity price data.
    // Logic is incorrect as is now
    public void syncWeatherData() {
        try {
            // Get all dates that have electricity price data but no weather data
            List<LocalDate> datesNeedingWeatherData = findDatesNeedingWeatherData();

            for (LocalDate date : datesNeedingWeatherData) {
                fetchAndSaveWeatherDataForDate(date);
            }
        } catch (Exception e) {
            System.err.println("Error during weather data sync: " + e.getMessage());
        }
    }

    /**
     * Manually trigger weather data fetch for a specific date
     */
    public void fetchWeatherDataForDate(LocalDate date) {
        fetchAndSaveWeatherDataForDate(date);
    }

    private List<LocalDate> findDatesNeedingWeatherData() {
        // Get dates from electricity price data
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        List<LocalDate> electricityDates = electricityPriceRepository.findDistinctDatesBetween(startDate, endDate);

        // Filter out dates that already have weather data
        return electricityDates.stream()
                .filter(date -> weatherDataRepository.findByDate(date).isEmpty())
                .toList();
    }

    private void fetchAndSaveWeatherDataForDate(LocalDate date) {
        try {
            Double averageTemperature = weatherApiClient.fetchDailyAverageTemperature(date);
            if (averageTemperature != null) {
                WeatherData weatherData = new WeatherData(date, averageTemperature);
                weatherDataRepository.save(weatherData);
                System.out.println("Saved weather data for date: " + date + ", temp: " + averageTemperature);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch weather data for date " + date + ": " + e.getMessage());
        }
    }
}
