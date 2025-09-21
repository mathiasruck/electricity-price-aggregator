package com.mathias.electricitypriceaggregator.application.service;

import com.mathias.electricitypriceaggregator.domain.model.WeatherData;
import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import com.mathias.electricitypriceaggregator.domain.repository.WeatherDataRepository;
import com.mathias.electricitypriceaggregator.infrastructure.external.WeatherApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(WeatherService.class);
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
    @Scheduled(cron = "0 * * * * *")
    public void syncWeatherData() {
        try {
            List<LocalDate> pricesDateWithoutWeather = electricityPriceRepository.findPricesDateWithoutWeather();

            // todo next: open threads to fetch data in parallel
            pricesDateWithoutWeather
                    .forEach(this::fetchAndSaveWeatherDataForDate);
        } catch (Exception e) {
            LOG.error("Error during weather data sync: {}", e.getMessage());
        }
    }

    private void fetchAndSaveWeatherDataForDate(LocalDate date) {
        try {
            List<Integer> recordedHours = electricityPriceRepository.findRecordedHoursByDate(date);
            Double averageTemperature = weatherApiClient.fetchDailyAverageTemperature(date, recordedHours);
            if (averageTemperature != null) {
                WeatherData weatherData = new WeatherData(date, averageTemperature);
                weatherDataRepository.save(weatherData);
                LOG.debug("Saved weather data for date: {}, temp: {}", date, averageTemperature);
            }
        } catch (Exception e) {
            LOG.error("Failed to fetch weather data for date {}: {}", date, e.getMessage());
        }
    }
}
