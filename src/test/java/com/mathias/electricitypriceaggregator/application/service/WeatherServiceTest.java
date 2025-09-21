package com.mathias.electricitypriceaggregator.application.service;

import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import com.mathias.electricitypriceaggregator.domain.repository.WeatherDataRepository;
import com.mathias.electricitypriceaggregator.infrastructure.external.WeatherApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @Mock
    private ElectricityPriceRepository electricityPriceRepository;

    @Mock
    private WeatherApiClient weatherApiClient;


    @Test
    public void syncWeatherData_handlesNoDates() {
        when(electricityPriceRepository.findPricesDateWithoutWeather()).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> weatherService.syncWeatherData());
        verify(electricityPriceRepository).findPricesDateWithoutWeather();
        verifyNoMoreInteractions(electricityPriceRepository, weatherApiClient, weatherDataRepository);
    }

    @Test
    public void syncWeatherData_withDatesAndValidHourIndices() {
        var date = LocalDate.now();
        List<Integer> recordedHours = List.of(1, 2, 3);
        when(electricityPriceRepository.findPricesDateWithoutWeather()).thenReturn(List.of(date));
        when(electricityPriceRepository.findRecordedHoursByDate(date)).thenReturn(recordedHours);
        when(weatherApiClient.fetchDailyAverageTemperature(eq(date), eq(recordedHours))).thenReturn(10.0);

        weatherService.syncWeatherData();

        verify(electricityPriceRepository).findRecordedHoursByDate(date);
        verify(weatherApiClient).fetchDailyAverageTemperature(eq(date), eq(recordedHours));
        verify(weatherDataRepository).save(any());
    }

    @Test
    public void syncWeatherData_withEmptyHourIndices() {
        var date = LocalDate.now();
        List<Integer> recordedHours = Collections.emptyList();
        when(electricityPriceRepository.findPricesDateWithoutWeather()).thenReturn(List.of(date));
        when(electricityPriceRepository.findRecordedHoursByDate(date)).thenReturn(recordedHours);
        when(weatherApiClient.fetchDailyAverageTemperature(eq(date), eq(recordedHours))).thenReturn(null);

        weatherService.syncWeatherData();

        verify(electricityPriceRepository).findRecordedHoursByDate(date);
        verify(weatherApiClient).fetchDailyAverageTemperature(eq(date), eq(recordedHours));
        verifyNoInteractions(weatherDataRepository);
    }

    @Test
    public void syncWeatherData_apiClientThrows() {
        var date = LocalDate.now();
        List<Integer> recordedHours = List.of(1, 2, 3);

        when(electricityPriceRepository.findPricesDateWithoutWeather()).thenReturn(List.of(date));
        when(electricityPriceRepository.findRecordedHoursByDate(date)).thenReturn(recordedHours);
        when(weatherApiClient.fetchDailyAverageTemperature(eq(date), eq(recordedHours)))
                .thenThrow(new RuntimeException("API error"));

        weatherService.syncWeatherData();

        verify(electricityPriceRepository).findRecordedHoursByDate(date);
        verifyNoInteractions(weatherDataRepository);
    }

    @Test
    public void syncWeatherData_withMultipleDates() {
        var date1 = LocalDate.now();
        var date2 = LocalDate.now().minusDays(1);
        List<Integer> recordedHours1 = List.of(1, 2, 3);
        List<Integer> recordedHours2 = List.of(4, 5, 6);

        when(electricityPriceRepository.findPricesDateWithoutWeather()).thenReturn(List.of(date1, date2));
        when(electricityPriceRepository.findRecordedHoursByDate(date1)).thenReturn(recordedHours1);
        when(electricityPriceRepository.findRecordedHoursByDate(date2)).thenReturn(recordedHours2);
        when(weatherApiClient.fetchDailyAverageTemperature(eq(date1), eq(recordedHours1))).thenReturn(10.0);
        when(weatherApiClient.fetchDailyAverageTemperature(eq(date2), eq(recordedHours2))).thenReturn(12.0);

        weatherService.syncWeatherData();

        verify(electricityPriceRepository).findRecordedHoursByDate(date1);
        verify(electricityPriceRepository).findRecordedHoursByDate(date2);
        verify(weatherApiClient).fetchDailyAverageTemperature(eq(date1), eq(recordedHours1));
        verify(weatherApiClient).fetchDailyAverageTemperature(eq(date2), eq(recordedHours2));
        verify(weatherDataRepository, times(2)).save(any());
    }

    @Test
    public void syncWeatherData_apiReturnsNoTemperatureData() {
        var date = LocalDate.now();
        List<Integer> recordedHours = List.of(1, 2, 3);
        when(electricityPriceRepository.findPricesDateWithoutWeather()).thenReturn(List.of(date));
        when(electricityPriceRepository.findRecordedHoursByDate(date)).thenReturn(recordedHours);
        when(weatherApiClient.fetchDailyAverageTemperature(eq(date), eq(recordedHours))).thenReturn(null);

        weatherService.syncWeatherData();

        verify(electricityPriceRepository).findRecordedHoursByDate(date);
        verify(weatherApiClient).fetchDailyAverageTemperature(eq(date), eq(recordedHours));
        verifyNoInteractions(weatherDataRepository);
    }

    @Test
    public void syncWeatherData_withMixedResults() {
        var date1 = LocalDate.of(2025, 9, 21);
        var date2 = LocalDate.of(2025, 9, 20);
        List<Integer> recordedHours1 = List.of(1, 2, 3);
        List<Integer> recordedHours2 = List.of(4, 5, 6);

        when(electricityPriceRepository.findPricesDateWithoutWeather()).thenReturn(List.of(date1, date2));
        when(electricityPriceRepository.findRecordedHoursByDate(date1)).thenReturn(recordedHours1);
        when(electricityPriceRepository.findRecordedHoursByDate(date2)).thenReturn(recordedHours2);
        when(weatherApiClient.fetchDailyAverageTemperature(eq(date1), eq(recordedHours1))).thenReturn(10.0);
        when(weatherApiClient.fetchDailyAverageTemperature(eq(date2), eq(recordedHours2)))
                .thenThrow(new RuntimeException("API error for second date"));

        weatherService.syncWeatherData();

        verify(electricityPriceRepository).findRecordedHoursByDate(date1);
        verify(electricityPriceRepository).findRecordedHoursByDate(date2);
        verify(weatherApiClient).fetchDailyAverageTemperature(eq(date1), eq(recordedHours1));
        verify(weatherApiClient).fetchDailyAverageTemperature(eq(date2), eq(recordedHours2));
        verify(weatherDataRepository).save(any());
    }

    @Test
    public void syncWeatherData_withMixedNullResults() {
        var date1 = LocalDate.of(2025, 9, 21);
        var date2 = LocalDate.of(2025, 9, 20);
        List<Integer> recordedHours1 = List.of(1, 2, 3);
        List<Integer> recordedHours2 = List.of(4, 5, 6);

        when(electricityPriceRepository.findPricesDateWithoutWeather()).thenReturn(List.of(date1, date2));
        when(electricityPriceRepository.findRecordedHoursByDate(date1)).thenReturn(recordedHours1);
        when(electricityPriceRepository.findRecordedHoursByDate(date2)).thenReturn(recordedHours2);
        when(weatherApiClient.fetchDailyAverageTemperature(eq(date1), eq(recordedHours1))).thenReturn(10.0);
        when(weatherApiClient.fetchDailyAverageTemperature(eq(date2), eq(recordedHours2))).thenReturn(null);

        weatherService.syncWeatherData();

        verify(electricityPriceRepository).findRecordedHoursByDate(date1);
        verify(electricityPriceRepository).findRecordedHoursByDate(date2);
        verify(weatherApiClient).fetchDailyAverageTemperature(eq(date1), eq(recordedHours1));
        verify(weatherApiClient).fetchDailyAverageTemperature(eq(date2), eq(recordedHours2));
        verify(weatherDataRepository).save(any());
    }
}
