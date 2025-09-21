package com.mathias.electricitypriceaggregator.web.controller;

import com.mathias.electricitypriceaggregator.application.service.AggregationService;
import com.mathias.electricitypriceaggregator.domain.valueobject.DailyAggregatedData;
import com.mathias.electricitypriceaggregator.web.dto.DailyAggregatedDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for aggregated data operations
 */
@RestController
@RequestMapping("/api/v1/aggregated-data")
@Tag(name = "Aggregated Data", description = "Operations for retrieving aggregated electricity price and weather data")
public class AggregatedDataController {

    private final AggregationService aggregationService;

    public AggregatedDataController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping
    @Operation(summary = "Get aggregated data for date range",
            description = "Retrieve daily aggregated electricity prices and weather data for the specified date range")
    public ResponseEntity<List<DailyAggregatedDataDto>> getAggregatedData(
            @Parameter(description = "Start date (YYYY-MM-DD) in UTC", example = "2024-01-01")
            @RequestParam("startDateUtc") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateUtc,

            @Parameter(description = "End date (YYYY-MM-DD) in UTC", example = "2024-01-31")
            @RequestParam("endDateUtc") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDateUtc) {

        try {
            if (startDateUtc.isAfter(endDateUtc)) {
                return ResponseEntity.badRequest().build();
            }

            List<DailyAggregatedData> aggregatedData = aggregationService.getAggregatedData(startDateUtc, endDateUtc);

            List<DailyAggregatedDataDto> dtoList = aggregatedData.stream()
                    .map(data -> new DailyAggregatedDataDto(
                            data.date(),
                            data.averageElectricityPrice(),
                            data.averageTemperature()))
                    .toList();

            return ResponseEntity.ok(dtoList);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
