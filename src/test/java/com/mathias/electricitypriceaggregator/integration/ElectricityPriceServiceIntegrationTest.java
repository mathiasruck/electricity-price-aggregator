package com.mathias.electricitypriceaggregator.integration;

import com.mathias.electricitypriceaggregator.application.service.ElectricityPriceService;
import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for electricity price CSV upload functionality
 */
public class ElectricityPriceServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ElectricityPriceService electricityPriceService;

    @Autowired
    private ElectricityPriceRepository electricityPriceRepository;

    @Test
    void shouldProcessCsvUploadSuccessfully() {
        // Given
        String csvContent = """
                "Ajatempel (UTC)";"Kuup\uFFFDev (Eesti aeg)";"NPS L\uFFFDti";"NPS Leedu";"NPS Soome";"NPS Eesti"
                "1703948400";"30.12.2023 15:00";"41,20";"41,20";"41,20";"41,20"
                "1703952000";"30.12.2023 16:00";"39,80";"39,80";"39,80";"39,80"
                "1704034800";"31.12.2023 15:00";"40,01";"40,01";"40,01";"40,01"
                "1704038400";"31.12.2023 16:00";"38,37";"38,37";"38,37";"38,37"
                "1704121200";"01.01.2024 15:00";"42,50";"42,50";"42,50";"42,50"
                "1704124800";"01.01.2024 16:00";"39,75";"39,75";"39,75";"39,75"
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // When
        electricityPriceService.processCsvUpload(file);

        // Then
        var startDate = LocalDate.of(2023, 12, 31);
        var endDate = LocalDate.of(2023, 12, 31);
        var prices = electricityPriceRepository.findByDateBetween(startDate, endDate)
                .stream()
                .sorted(Comparator.comparing(p -> p.getRecordedAt()))
                .toList();

        assertThat(prices).hasSize(2);
        assertThat(prices.get(0).getPrice()).isEqualTo(40.01);
        assertThat(prices.get(0).getRecordedAt()).isEqualTo(1704034800);
        assertThat(prices.get(1).getPrice()).isEqualTo(38.37);
        assertThat(prices.get(1).getRecordedAt()).isEqualTo(1704038400);
    }
}
