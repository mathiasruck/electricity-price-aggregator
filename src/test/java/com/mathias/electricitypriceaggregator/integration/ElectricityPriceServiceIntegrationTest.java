package com.mathias.electricitypriceaggregator.integration;

import com.mathias.electricitypriceaggregator.application.service.ElectricityPriceService;
import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for electricity price CSV upload functionality
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ElectricityPriceServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ElectricityPriceService electricityPriceService;

    @Autowired
    private ElectricityPriceRepository electricityPriceRepository;

    @Test
    void shouldProcessCsvUploadSuccessfully() {
        // Given
        String csvContent = """
                "Ajatempel (UTC)";"Kuupäev (Eesti aeg)";"NPS Läti";"NPS Leedu";"NPS Soome";"NPS Eesti"
                "1704060000";"01.01.2024 00:00";"40,01";"40,01";"40,01";"40,01"
                "1704063600";"01.01.2024 01:00";"38,37";"38,37";"38,37";"38,37"
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
        var prices = electricityPriceRepository.findByDateBetween(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );

        assertThat(prices).hasSize(2);
        assertThat(prices.get(0).getNpsEstonia()).isEqualTo(40.01);
        assertThat(prices.get(1).getNpsEstonia()).isEqualTo(38.37);
    }
}
