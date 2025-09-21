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
import static org.mockito.Mockito.when;
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
    void shouldReturnAggregatedDataSuccessfully() throws Exception {
        // Given
        LocalDate date = LocalDate.of(2024, 1, 1);
        List<DailyAggregatedData> mockData = List.of(
                new DailyAggregatedData(date, 40.0, 5.5)
        );

        when(aggregationService.getAggregatedData(any(), any())).thenReturn(mockData);

        // When & Then
        mockMvc.perform(get("/api/v1/aggregated-data")
                        .param("startDateUtc", "2024-01-01")
                        .param("endDateUtc", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2024-01-01"))
                .andExpect(jsonPath("$[0].averageElectricityPrice").value(40.0))
                .andExpect(jsonPath("$[0].averageTemperature").value(5.5));
    }
}
