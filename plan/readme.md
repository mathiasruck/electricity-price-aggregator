Create a Spring Boot Java project using Java 17 and Maven with the following structure and features:

Project Name: weather-electricity-aggregator

# Dependencies:
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- OpenCSV (for CSV parsing)
- springdoc-openapi (for Swagger documentation)
- Testcontainers (for PostgreSQL integration testing)
- JUnit 5
- Mockito

# Architecture:
Use a Hybrid Domain-Driven Design (DDD) + Layered Architecture with these base packages:
- com.example.weather.domain: contains domain entities, value objects, and repository interfaces
- com.example.weather.application: contains use-case services
- com.example.weather.infrastructure: contains implementations for persistence, external API clients, and scheduling
- com.example.weather.web: contains REST controllers

# Features:
1. Upload CSV of electricity prices (NPS Eesti) using a POST endpoint
2. Upsert records into PostgreSQL using JPA
3. Periodically fetch weather data from Open Meteo Historical API (hourly temp, calculate daily average)
4. Store weather data in DB (if price data exists for that date)
5. Provide REST API to retrieve aggregated data (date, price, avg temperature) by date range
6. Enable Swagger UI at `/swagger-ui.html`
7. Add integration test setup using Testcontainers for PostgreSQL

# Docker:
- Include Dockerfile for the backend
- Include docker-compose.yml with PostgreSQL and the backend

# Optional:
- Use WebClient for external API calls
- Use Spring Scheduler to run weather sync every minute

# Instructions:
Generate all base classes, interfaces, and configurations to set up the project skeleton and demonstrate the key flows.

