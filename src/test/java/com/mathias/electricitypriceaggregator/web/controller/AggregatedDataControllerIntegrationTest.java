package com.mathias.electricitypriceaggregator.web.controller;

import com.mathias.electricitypriceaggregator.application.service.AggregationService;
import com.mathias.electricitypriceaggregator.domain.valueobject.DailyAggregatedData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for AggregatedDataController
 */
@AutoConfigureWebMvc
@AutoConfigureJson
@WebMvcTest(AggregatedDataController.class)
public class AggregatedDataControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AggregationService aggregationService;

    @Test
    void shouldReturn_aggregatedDataSuccessfully() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        List<DailyAggregatedData> mockData = List.of(
                new DailyAggregatedData(date, 40.0, 5.5)
        );

        when(aggregationService.getAggregatedData(any(), any())).thenReturn(mockData);

        mockMvc.perform(get("/api/v1/aggregated-data")
                        .param("startDateUtc", "2024-01-01")
                        .param("endDateUtc", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2024-01-01"))
                .andExpect(jsonPath("$[0].averageElectricityPrice").value(40.0))
                .andExpect(jsonPath("$[0].averageTemperature").value(5.5));
    }

    @Test
    void shouldReturnEmptyList_whenNoDataAvailable() throws Exception {
        when(aggregationService.getAggregatedData(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/aggregated-data")
                        .param("startDateUtc", "2024-01-01")
                        .param("endDateUtc", "2024-01-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnBadRequest_whenStartDateIsAfterEndDate() throws Exception {
        mockMvc.perform(get("/api/v1/aggregated-data")
                        .param("startDateUtc", "2024-01-02")
                        .param("endDateUtc", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenDatesAreMissing() throws Exception {
        mockMvc.perform(get("/api/v1/aggregated-data"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenDatesAreInvalidFormat() throws Exception {
        mockMvc.perform(get("/api/v1/aggregated-data")
                        .param("startDateUtc", "invalid-date")
                        .param("endDateUtc", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnMultipleDaysOfData() throws Exception {
        List<DailyAggregatedData> mockData = List.of(
                new DailyAggregatedData(LocalDate.of(2024, 1, 1), 40.0, 5.5),
                new DailyAggregatedData(LocalDate.of(2024, 1, 2), 42.5, 6.0),
                new DailyAggregatedData(LocalDate.of(2024, 1, 3), 38.0, 4.5)
        );

        when(aggregationService.getAggregatedData(any(), any())).thenReturn(mockData);

        mockMvc.perform(get("/api/v1/aggregated-data")
                        .param("startDateUtc", "2024-01-01")
                        .param("endDateUtc", "2024-01-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].date").value("2024-01-01"))
                .andExpect(jsonPath("$[0].averageElectricityPrice").value(40.0))
                .andExpect(jsonPath("$[0].averageTemperature").value(5.5))
                .andExpect(jsonPath("$[1].date").value("2024-01-02"))
                .andExpect(jsonPath("$[1].averageElectricityPrice").value(42.5))
                .andExpect(jsonPath("$[1].averageTemperature").value(6.0))
                .andExpect(jsonPath("$[2].date").value("2024-01-03"))
                .andExpect(jsonPath("$[2].averageElectricityPrice").value(38.0))
                .andExpect(jsonPath("$[2].averageTemperature").value(4.5));
    }

    @Test
    void shouldVerifyServiceInteractionWithCorrectDates() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 3);
        when(aggregationService.getAggregatedData(startDate, endDate))
                .thenReturn(List.of(new DailyAggregatedData(startDate, 40.0, 5.5)));

        mockMvc.perform(get("/api/v1/aggregated-data")
                        .param("startDateUtc", "2024-01-01")
                        .param("endDateUtc", "2024-01-03"))
                .andExpect(status().isOk());

        verify(aggregationService, times(1)).getAggregatedData(startDate, endDate);
    }

    @Test
    void shouldHandleServiceException() throws Exception {
        when(aggregationService.getAggregatedData(any(), any()))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/v1/aggregated-data")
                        .param("startDateUtc", "2024-01-01")
                        .param("endDateUtc", "2024-01-01"))
                .andExpect(status().isInternalServerError());
    }
}
