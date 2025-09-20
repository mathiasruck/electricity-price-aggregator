package com.mathias.electricitypriceaggregator.application.service;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.domain.repository.ElectricityPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Application service for handling electricity price data operations
 */
@Service
@Transactional
public class ElectricityPriceService {

    private final ElectricityPriceRepository electricityPriceRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public ElectricityPriceService(ElectricityPriceRepository electricityPriceRepository) {
        this.electricityPriceRepository = electricityPriceRepository;
    }

    /**
     * Process and save electricity price data from CSV upload
     */
    public void processCsvUpload(MultipartFile file) {
        try {
            List<ElectricityPrice> electricityPrices = parseCsvFile(file);
            upsertElectricityPrices(electricityPrices);
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage(), e);
        }
    }

    private List<ElectricityPrice> parseCsvFile(MultipartFile file) throws IOException, CsvException {
        List<ElectricityPrice> electricityPrices = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = csvReader.readAll();

            // Skip header row
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                if (record.length >= 6) {
                    String dateTimeStr = record[1]; // "Kuupäev (Eesti aeg)" column
                    String npsEstoniaStr = record[5]; // "NPS Eesti" column

                    try {
                        LocalDateTime timestamp = LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);
                        Double npsEstonia = parsePrice(npsEstoniaStr);

                        electricityPrices.add(new ElectricityPrice(timestamp, npsEstonia));
                    } catch (Exception e) {
                        // Log and skip invalid records
                        System.err.println("Skipping invalid record: " + String.join(",", record) + " - " + e.getMessage());
                    }
                }
            }
        }

        return electricityPrices;
    }

    private Double parsePrice(String priceStr) {
        // Handle comma as decimal separator and remove any extra whitespace
        return Double.parseDouble(priceStr.trim().replace(",", "."));
    }

    private void upsertElectricityPrices(List<ElectricityPrice> electricityPrices) {
        for (ElectricityPrice price : electricityPrices) {
            // Check if record exists
            var existing = electricityPriceRepository.findByTimestamp(price.getTimestamp());
            if (existing.isPresent()) {
                // Update existing record
                existing.get().setNpsEstonia(price.getNpsEstonia());
                electricityPriceRepository.save(existing.get());
            } else {
                // Insert new record
                electricityPriceRepository.save(price);
            }
        }
    }
}
