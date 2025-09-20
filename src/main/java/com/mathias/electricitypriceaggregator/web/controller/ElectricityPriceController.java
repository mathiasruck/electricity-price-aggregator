package com.mathias.electricitypriceaggregator.web.controller;

import com.mathias.electricitypriceaggregator.application.service.ElectricityPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for electricity price operations
 */
@RestController
@RequestMapping("/api/v1/electricity-prices")
@Tag(name = "Electricity Prices", description = "Operations for managing electricity price data")
public class ElectricityPriceController {

    private final ElectricityPriceService electricityPriceService;

    public ElectricityPriceController(ElectricityPriceService electricityPriceService) {
        this.electricityPriceService = electricityPriceService;
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload electricity price data from CSV file",
               description = "Upload a CSV file containing historical electricity price data. The file should contain NPS Estonia price data.")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                return ResponseEntity.badRequest().body("File must be a CSV file");
            }

            electricityPriceService.processCsvUpload(file);
            return ResponseEntity.ok("CSV file processed successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }
}
