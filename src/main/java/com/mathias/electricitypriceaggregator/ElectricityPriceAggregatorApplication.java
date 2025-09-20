package com.mathias.electricitypriceaggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ElectricityPriceAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectricityPriceAggregatorApplication.class, args);
    }
}
