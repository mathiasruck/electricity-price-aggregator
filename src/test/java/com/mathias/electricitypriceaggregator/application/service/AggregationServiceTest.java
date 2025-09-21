package com.mathias.electricitypriceaggregator.application.service;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.domain.model.ElectricityPriceEstonia;
import com.mathias.electricitypriceaggregator.domain.model.WeatherData;
import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import com.mathias.electricitypriceaggregator.domain.repository.WeatherDataRepository;
import com.mathias.electricitypriceaggregator.domain.valueobject.DailyAggregatedData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AggregationServiceTest {
    
    @InjectMocks
    private AggregationService aggregationService;

    @Mock
    private ElectricityPriceRepository electricityPriceRepository;
    @Mock
    private WeatherDataRepository weatherDataRepository;

    @Test
    public void getAggregatedData_returnsEmptyListWhenNoData() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);

        when(electricityPriceRepository.findByDateBetween(startDate, endDate)).thenReturn(Collections.emptyList());
        when(weatherDataRepository.findByDateBetween(startDate, endDate)).thenReturn(Collections.emptyList());

        List<DailyAggregatedData> result = aggregationService.getAggregatedData(startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(electricityPriceRepository).findByDateBetween(startDate, endDate);
        verify(weatherDataRepository).findByDateBetween(startDate, endDate);
    }

    @Test
    public void getAggregatedData_withSampleData() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalDateTime timestamp = date.atStartOfDay();

        ElectricityPrice price1 = new ElectricityPriceEstonia();
        price1.setRecordedAt(timestamp.toEpochSecond(ZoneOffset.UTC));
        price1.setPrice(100.0);

        ElectricityPrice price2 = new ElectricityPriceEstonia();
        price2.setRecordedAt(timestamp.plusHours(12).toEpochSecond(ZoneOffset.UTC));
        price2.setPrice(200.0);

        WeatherData weather = new WeatherData(date, 20.0);


        when(electricityPriceRepository.findByDateBetween(date, date))
                .thenReturn(List.of(price1, price2));
        when(weatherDataRepository.findByDateBetween(date, date))
                .thenReturn(List.of(weather));

        List<DailyAggregatedData> result = aggregationService.getAggregatedData(date, date);

        assertNotNull(result);
        assertEquals(1, result.size());
        DailyAggregatedData aggregated = result.get(0);
        assertEquals(date, aggregated.date());
        assertEquals(150.0, aggregated.averageElectricityPrice());
        assertEquals(20.0, aggregated.averageTemperature());
    }

    @Test
    public void getAggregatedData_withMultipleDays() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 3);

        ElectricityPriceEstonia price1 = new ElectricityPriceEstonia();
        price1.setRecordedAt(startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        price1.setPrice(100.0);

        ElectricityPriceEstonia price2 = new ElectricityPriceEstonia();
        price2.setRecordedAt(startDate.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        price2.setPrice(200.0);

        WeatherData weather1 = new WeatherData(startDate, 20.0);
        WeatherData weather2 = new WeatherData(startDate.plusDays(1), 22.0);

        when(electricityPriceRepository.findByDateBetween(startDate, endDate))
                .thenReturn(List.of(price1, price2));
        when(weatherDataRepository.findByDateBetween(startDate, endDate))
                .thenReturn(List.of(weather1, weather2));

        List<DailyAggregatedData> result = aggregationService.getAggregatedData(startDate, endDate);

        assertEquals(2, result.size());
        assertEquals(100.0, result.get(0).averageElectricityPrice());
        assertEquals(20.0, result.get(0).averageTemperature());
        assertEquals(200.0, result.get(1).averageElectricityPrice());
        assertEquals(22.0, result.get(1).averageTemperature());
    }

    @Test
    public void getAggregatedData_withOnlyElectricityPrices() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        ElectricityPriceEstonia price = new ElectricityPriceEstonia();
        price.setRecordedAt(date.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        price.setPrice(150.0);

        when(electricityPriceRepository.findByDateBetween(date, date)).thenReturn(List.of(price));
        when(weatherDataRepository.findByDateBetween(date, date)).thenReturn(Collections.emptyList());

        List<DailyAggregatedData> result = aggregationService.getAggregatedData(date, date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(150.0, result.get(0).averageElectricityPrice());
        assertNull(result.get(0).averageTemperature());
    }

    @Test
    public void getAggregatedData_withOnlyWeatherData() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        WeatherData weather = new WeatherData(date, 18.5);

        when(electricityPriceRepository.findByDateBetween(date, date)).thenReturn(Collections.emptyList());
        when(weatherDataRepository.findByDateBetween(date, date)).thenReturn(List.of(weather));

        List<DailyAggregatedData> result = aggregationService.getAggregatedData(date, date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).averageElectricityPrice());
        assertEquals(18.5, result.get(0).averageTemperature());
    }

    @Test
    public void getAggregatedData_withNullStartDate() {
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                aggregationService.getAggregatedData(null, endDate));
        assertEquals("Start date cannot be null", exception.getMessage());
    }

    @Test
    public void testGet_aggregatedData_withNullEndDate() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                aggregationService.getAggregatedData(startDate, null));
        assertEquals("End date cannot be null", exception.getMessage());
    }

    @Test
    public void getAggregatedData_withInvalidDateRange() {
        LocalDate startDate = LocalDate.of(2024, 1, 2);
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                aggregationService.getAggregatedData(startDate, endDate));
        assertEquals("End date 2024-01-01 cannot be before start date 2024-01-02", exception.getMessage());
    }

    @Test
    public void getAggregatedData_withMultipleRecordsPerDay() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalDateTime baseTime = date.atStartOfDay();

        // Create multiple electricity prices for the same day
        ElectricityPriceEstonia price1 = new ElectricityPriceEstonia();
        price1.setRecordedAt(baseTime.toEpochSecond(ZoneOffset.UTC));
        price1.setPrice(100.0);

        ElectricityPriceEstonia price2 = new ElectricityPriceEstonia();
        price2.setRecordedAt(baseTime.plusHours(6).toEpochSecond(ZoneOffset.UTC));
        price2.setPrice(200.0);

        ElectricityPriceEstonia price3 = new ElectricityPriceEstonia();
        price3.setRecordedAt(baseTime.plusHours(12).toEpochSecond(ZoneOffset.UTC));
        price3.setPrice(300.0);

        when(electricityPriceRepository.findByDateBetween(date, date))
                .thenReturn(List.of(price1, price2, price3));
        when(weatherDataRepository.findByDateBetween(date, date))
                .thenReturn(Collections.emptyList());

        List<DailyAggregatedData> result = aggregationService.getAggregatedData(date, date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(200.0, result.get(0).averageElectricityPrice());
    }

    @Test
    public void getAggregatedData_forGapsInData() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 3);

        // Only add data for first and last day, creating a gap
        ElectricityPriceEstonia price1 = new ElectricityPriceEstonia();
        price1.setRecordedAt(startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        price1.setPrice(100.0);

        ElectricityPriceEstonia price2 = new ElectricityPriceEstonia();
        price2.setRecordedAt(endDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        price2.setPrice(300.0);

        WeatherData weather1 = new WeatherData(startDate, 20.0);
        WeatherData weather3 = new WeatherData(endDate, 22.0);

        when(electricityPriceRepository.findByDateBetween(startDate, endDate))
                .thenReturn(List.of(price1, price2));
        when(weatherDataRepository.findByDateBetween(startDate, endDate))
                .thenReturn(List.of(weather1, weather3));

        List<DailyAggregatedData> result = aggregationService.getAggregatedData(startDate, endDate);

        assertEquals(2, result.size());

        assertEquals(startDate, result.get(0).date());
        assertEquals(100.0, result.get(0).averageElectricityPrice());
        assertEquals(20.0, result.get(0).averageTemperature());

        assertEquals(endDate, result.get(1).date());
        assertEquals(300.0, result.get(1).averageElectricityPrice());
        assertEquals(22.0, result.get(1).averageTemperature());
    }
}
