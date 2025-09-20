package com.mathias.electricitypriceaggregator.web.controller;

import com.mathias.electricitypriceaggregator.application.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for weather data operations
 */
@RestController
@RequestMapping("/api/v1/weather")
@Tag(name = "Weather Data", description = "Operations for managing weather data")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/fetch/{date}")
    @Operation(summary = "Manually fetch weather data for a specific date",
               description = "Trigger manual fetch of weather data from Open Meteo API for the specified date")
    public ResponseEntity<String> fetchWeatherData(
            @Parameter(description = "Date to fetch weather data for (YYYY-MM-DD)", example = "2024-01-15")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            weatherService.fetchWeatherDataForDate(date);
            return ResponseEntity.ok("Weather data fetch triggered for date: " + date);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching weather data: " + e.getMessage());
        }
    }
}
