package com.mathias.electricitypriceaggregator.application.service;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.repository.ElectricityPriceBulkRepository;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
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

    private final ElectricityPriceBulkRepository electricityPriceRepository;

    public ElectricityPriceService(ElectricityPriceBulkRepository electricityPriceRepository) {
        this.electricityPriceRepository = electricityPriceRepository;
    }

    /**
     * Process and save electricity price data from CSV upload
     */
    public void processCsvUpload(MultipartFile file) {
        try {
            List<ElectricityPrice> electricityPrices = parseCsvFile(file);
            upsertElectricityPrices(electricityPrices);
        } catch (IOException e) {
            // todo improve error handling. Logs?
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

            return new CsvToBeanBuilder<ElectricityPrice>(csvReader)
                    .withType(ElectricityPrice.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withFilter(line -> line.length >= 1 && !line[0].isBlank())// todo need this? Improve validation and filtering?
                    .build()
                    .parse();

            // todo improve error handling
            //} catch (RuntimeException | IOException e) {
            //    throw new CsvParsingException("Failed to parse CSV file", e);
        }
    }

    private void upsertElectricityPrices(List<ElectricityPrice> electricityPrices) {
        electricityPriceRepository.upsertAll(electricityPrices);
    }
}
