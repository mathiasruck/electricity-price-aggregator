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

/**
 * Application service for aggregating electricity price and weather data
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
     * Get aggregated data for a date range
     */
    public List<DailyAggregatedData> getAggregatedData(LocalDate startDate, LocalDate endDate) {
        // Fetch electricity prices for the date range
        List<ElectricityPrice> electricityPrices = electricityPriceRepository.findByDateBetween(startDate, endDate);

        // Fetch weather data for the date range
        List<WeatherData> weatherDataList = weatherDataRepository.findByDateBetween(startDate, endDate);

        // Group electricity prices by date and calculate daily averages
        Map<Long, Double> dailyPriceAverages = electricityPrices.stream()
                .collect(Collectors.groupingBy(
                        ElectricityPrice::getTimestamp,
                        Collectors.averagingDouble(ElectricityPrice::getNpsEstonia)
                ));

        // Create a map of weather data by date
        Map<LocalDate, Double> weatherByDate = weatherDataList.stream()
                .collect(Collectors.toMap(
                        WeatherData::getDate,
                        WeatherData::getAverageTemperature
                ));

        // Create aggregated data for each date
        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> new DailyAggregatedData(
                        date,
                        // todo fix this mapping - should match types. Today is LocalDate, but electricity price uses timestamp as Long
                        dailyPriceAverages.get(date),
                        weatherByDate.get(date)
                ))
                .filter(data -> data.getAverageElectricityPrice() != null || data.getAverageTemperature() != null)
                .collect(Collectors.toList());
    }
}
