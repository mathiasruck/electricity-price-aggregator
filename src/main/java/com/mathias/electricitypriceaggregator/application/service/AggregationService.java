package com.mathias.electricitypriceaggregator.application.service;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.domain.model.WeatherData;
import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import com.mathias.electricitypriceaggregator.domain.repository.WeatherDataRepository;
import com.mathias.electricitypriceaggregator.domain.valueobject.DailyAggregatedData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.Instant.ofEpochSecond;
import static java.time.ZoneOffset.UTC;

/**
 * Application service for aggregating electricity price and weather data.
 * Provides functionality to retrieve aggregated daily data for electricity prices and weather.
 */
@Service
@Transactional(readOnly = true)
public class AggregationService {

    private final ElectricityPriceRepository electricityPriceRepository;
    private final WeatherDataRepository weatherDataRepository;

    public AggregationService(ElectricityPriceRepository electricityPriceRepository,
                              WeatherDataRepository weatherDataRepository) {
        this.electricityPriceRepository = electricityPriceRepository;
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Get aggregated data for a date range.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate   the end date of the range (inclusive)
     * @return list of daily aggregated data within the range, excluding days without any data
     * @throws IllegalArgumentException if startDate is null, endDate is null, or endDate is before startDate
     */
    public List<DailyAggregatedData> getAggregatedData(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        List<ElectricityPrice> electricityPrices = electricityPriceRepository.findByDateBetween(startDate, endDate);
        List<WeatherData> weatherDataList = weatherDataRepository.findByDateBetween(startDate, endDate);

        if (electricityPrices.isEmpty() && weatherDataList.isEmpty()) {
            return List.of();
        }

        // Group electricity prices by date and calculate daily averages
        Map<LocalDate, Double> dailyPriceAverages = electricityPrices.stream()
                .collect(Collectors.groupingBy(
                        electricityPrice -> ofEpochSecond(electricityPrice.getRecordedAt())
                                .atZone(UTC)
                                .toLocalDate(),
                        Collectors.averagingDouble(ElectricityPrice::getPrice)
                ));

        // Create a map of weather data by date
        Map<LocalDate, Double> weatherByDate = weatherDataList.stream()
                .collect(Collectors.toMap(
                        WeatherData::getDate,
                        WeatherData::getAverageTemperature
                ));

        // Create aggregated data for each date, filtering out days with no data
        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> createDailyAggregatedData(date, dailyPriceAverages, weatherByDate))
                .filter(data -> data.averageElectricityPrice() != null || data.averageTemperature() != null)
                .collect(Collectors.toList());
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    String.format("End date %s cannot be before start date %s", endDate, startDate));
        }
    }

    private DailyAggregatedData createDailyAggregatedData(LocalDate date, Map<LocalDate, Double> dailyPriceAverages,
                                                          Map<LocalDate, Double> weatherByDate) {
        Double price = dailyPriceAverages.get(date);
        Double temperature = weatherByDate.get(date);
        return new DailyAggregatedData(date, price, temperature);
    }
}
