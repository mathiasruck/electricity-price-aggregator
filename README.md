# Weather and Electricity Price Aggregator

A Spring Boot application that aggregates electricity price data from CSV uploads and weather data from the Open Meteo
API, providing a unified REST API for querying the combined data.

## Features

- **CSV Upload**: Upload electricity price data (NPS Estonia format)
- **Weather Integration**: Automatic fetching of weather data from Open Meteo API
- **Data Aggregation**: Combine electricity prices and weather data by date
- **REST API**: Query aggregated data by date range
- **Swagger Documentation**: Interactive API documentation
- **Docker Support**: Containerized deployment with PostgreSQL

## Architecture

The application follows a Hybrid Domain-Driven Design (DDD) + Layered Architecture:

- **Domain Layer**: Core business models and repository interfaces
- **Application Layer**: Use-case services and business logic
- **Infrastructure Layer**: JPA entities, external API clients, persistence
- **Web Layer**: REST controllers and DTOs

## Technology Stack

- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Web** (REST API)
- **Spring Data JPA** (Database access)
- **PostgreSQL** (Database)
- **OpenCSV** (CSV processing)
- **SpringDoc OpenAPI** (Swagger documentation)
- **Testcontainers** (Integration testing)
- **Docker & Docker Compose**

## Quick Start

### Prerequisites

- Java 17+
- Docker and Docker Compose
- Maven

### Running with Docker Compose

1. Clone the repository
2. Build the application:
   ```bash
   mvn clean package -DskipTests
   ```

3. Start the services:
   ```bash
   docker-compose up -d
   ```

4. Access the application:
    - API: http://localhost:8080
    - Swagger UI: http://localhost:8080/swagger-ui.html

### Running Locally

1. Start PostgreSQL database:
   ```bash
   docker run -d --name postgres -p 5432:5432 -e POSTGRES_DB=electricity_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:15-alpine
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

### Upload Electricity Price Data

```http
POST /api/v1/electricity-prices/upload
Content-Type: multipart/form-data

file: [CSV file with NPS Estonia data]
```

### Get Aggregated Data

```http
GET /api/v1/aggregated-data?startDate=2024-01-01&endDate=2024-01-31
```

### Manual Weather Data Fetch

```http
POST /api/v1/weather/fetch/2024-01-15
```

## CSV Format

The CSV file should contain the following columns:

- Column 1: "Ajatempel (UTC)" - Unix timestamp
- Column 2: "Kuupäev (Eesti aeg)" - Estonian datetime (dd.MM.yyyy HH:mm)
- Column 6: "NPS Eesti" - Estonia electricity price

Example:

```csv
"Ajatempel (UTC)";"Kuupäev (Eesti aeg)";"NPS Läti";"NPS Leedu";"NPS Soome";"NPS Eesti"
"1704060000";"01.01.2024 00:00";"40,01";"40,01";"40,01";"40,01"
```

## Weather Data

The application automatically fetches weather data from the Open Meteo Historical Weather API for Tallinn, Estonia
coordinates. Weather sync runs every minute to fetch data for dates that have electricity price data but no weather
data.

## Testing

Run unit and integration tests:

```bash
mvn test
```

Integration tests use Testcontainers to spin up a PostgreSQL database automatically.

## Database Schema

### electricity_price

- `id` (BIGINT, PK)
- `timestamp` (TIMESTAMP, UNIQUE)
- `date` (DATE)
- `nps_estonia` (DOUBLE)

### weather_data

- `id` (BIGINT, PK)
- `date` (DATE, UNIQUE)
- `average_temperature` (DOUBLE)

## Development

### Building

```bash
mvn clean compile
```

### Running Tests

```bash
mvn test
```

### Creating Docker Image

```bash
mvn clean package -DskipTests
docker build -t electricity-price-aggregator .
```

## Configuration

Key configuration properties in `application.yml`:

- Database connection settings
- File upload limits (10MB)
- Swagger/OpenAPI documentation paths
- Logging levels

## Contributing

1. Follow the existing package structure
2. Write tests for new features
3. Update documentation as needed
4. Follow Java coding conventions
