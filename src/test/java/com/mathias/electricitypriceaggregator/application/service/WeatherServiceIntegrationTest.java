package com.mathias.electricitypriceaggregator.application.service;

import com.mathias.electricitypriceaggregator.infrastructure.external.WeatherApiClient;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.ElectricityPriceEntity;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.entity.WeatherDataEntity;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.repository.JpaElectricityPriceRepository;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.repository.JpaWeatherDataRepository;
import com.mathias.electricitypriceaggregator.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;


class WeatherServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private JpaWeatherDataRepository weatherDataRepository;

    @Autowired
    private JpaElectricityPriceRepository electricityPriceRepository;

    @SpyBean
    private WeatherApiClient weatherApiClient;

    @BeforeEach
    void setUp() {
        weatherDataRepository.deleteAllInBatch();
        electricityPriceRepository.deleteAllInBatch();
    }

    @Test
    void syncWeatherData_withAvailablePriceData_savesWeatherData() {
        // Arrange
        ElectricityPriceEntity price = new ElectricityPriceEntity();
        price.setRecordedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        price.setPrice(100.0);
        price.setCountry("EE");
        electricityPriceRepository.save(price);

        when(weatherApiClient.fetchDailyAverageTemperature(any(LocalDate.class), anyList()))
                .thenReturn(20.5);

        // Act
        weatherService.syncWeatherData();

        // Assert
        List<WeatherDataEntity> savedData = weatherDataRepository.findAll();
        assertFalse(savedData.isEmpty());
        assertEquals(20.5, savedData.get(0).getAverageTemperature());
    }

    @Test
    void syncWeatherData_withNoAvailablePriceData_doesNotSaveWeatherData() {
        // Arrange
        when(weatherApiClient.fetchDailyAverageTemperature(any(LocalDate.class), anyList()))
                .thenReturn(20.5);

        // Act
        weatherService.syncWeatherData();

        // Assert
        List<WeatherDataEntity> savedData = weatherDataRepository.findAll();
        assertTrue(savedData.isEmpty());
    }

    @Test
    void syncWeatherData_withApiError_handlesGracefully() {
        // Arrange
        ElectricityPriceEntity price = new ElectricityPriceEntity();
        price.setRecordedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        price.setPrice(100.0);
        price.setCountry("EE");
        electricityPriceRepository.save(price);

        when(weatherApiClient.fetchDailyAverageTemperature(any(LocalDate.class), anyList()))
                .thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        assertDoesNotThrow(() -> weatherService.syncWeatherData());
        assertTrue(weatherDataRepository.findAll().isEmpty());
    }

    @Test
    void syncWeatherData_withMultipleDatesAndHours_SavesCorrectAverages() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        ElectricityPriceEntity price1 = new ElectricityPriceEntity();
        price1.setRecordedAt(now.toInstant(ZoneOffset.UTC));
        price1.setPrice(100.0);
        price1.setCountry("EE");

        ElectricityPriceEntity price2 = new ElectricityPriceEntity();
        price2.setRecordedAt(now.plusHours(1).toInstant(ZoneOffset.UTC));
        price2.setPrice(150.0);
        price2.setCountry("EE");

        electricityPriceRepository.saveAll(Arrays.asList(price1, price2));

        when(weatherApiClient.fetchDailyAverageTemperature(any(LocalDate.class), anyList()))
                .thenReturn(22.5);

        // Act
        weatherService.syncWeatherData();

        // Assert
        List<WeatherDataEntity> savedData = weatherDataRepository.findAll();
        assertEquals(1, savedData.size());
        assertEquals(22.5, savedData.get(0).getAverageTemperature());
    }
}