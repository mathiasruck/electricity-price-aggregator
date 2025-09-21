package com.mathias.electricitypriceaggregator.application.service;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.domain.model.ElectricityPriceEstonia;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.repository.ElectricityPriceBulkRepository;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Application service for handling electricity price data operations
 */
@Service
@Transactional
public class ElectricityPriceService {
    private static final Logger LOG = LoggerFactory.getLogger(ElectricityPriceService.class);

    private final ElectricityPriceBulkRepository electricityPriceBulkRepository;

    public ElectricityPriceService(ElectricityPriceBulkRepository electricityPriceBulkRepository) {
        this.electricityPriceBulkRepository = electricityPriceBulkRepository;
    }

    /**
     * Process and save electricity price data from CSV upload
     */
    @Transactional
    public void processCsvUpload(MultipartFile file) {
        try {
            List<ElectricityPrice> electricityPrices = parseCsvFile(file);
            LOG.debug("Parsed {} electricity prices from CSV", electricityPrices.size());
            upsertElectricityPrices(electricityPrices);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage(), e);
        }
    }

    public List<ElectricityPrice> parseCsvFile(MultipartFile file) throws IOException {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.ISO_8859_1)) {
            var parser = new CSVParserBuilder()
                    .withSeparator(';')
                    .build();
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(parser)
                    .build();

            var result = new CsvToBeanBuilder<ElectricityPrice>(csvReader)
                    .withType(ElectricityPriceEstonia.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withVerifyReader(true)
                    .withFilter(line -> line.length >= 1 && !line[0].isBlank())
                    .build()
                    .parse();

            LOG.debug("Found {} records in CSV file", result.size());
            return result;
        }
    }

    private void upsertElectricityPrices(List<ElectricityPrice> electricityPrices) {
        electricityPriceBulkRepository.upsertAll(electricityPrices);
    }
}
