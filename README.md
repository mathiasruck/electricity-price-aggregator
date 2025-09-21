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
2. Start the services:
   ```bash
   docker-compose up -d
   ```

3. Access the application:
    - API: http://localhost:8080
    - Swagger UI: http://localhost:8080/swagger-ui.html

### Running Locally

1. Start PostgreSQL database:
   ```bash
   docker run -d \
     --name postgres \
     -p 5432:5432 \
     -e POSTGRES_DB=electricity_db \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     postgres:15-alpine
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Documentation

### Upload Electricity Price Data

```http
POST /api/v1/electricity-prices/upload
Content-Type: multipart/form-data

Body:
- file: CSV file containing NPS Estonia price data
```

### Get Aggregated Data

```http
GET /api/v1/aggregated-data
Parameters:
  - startDate: YYYY-MM-DD (required)
  - endDate: YYYY-MM-DD (required)

Example: /api/v1/aggregated-data?startDate=2024-01-01&endDate=2024-01-31
```

## Data Formats

### Electricity Price CSV Format

The CSV file must be semicolon-separated and contain the following columns:

1. "Ajatempel (UTC)" - Unix timestamp
2. "Kuupäev (Eesti aeg)" - Estonian datetime (dd.MM.yyyy HH:mm)
3. "NPS Läti" - Latvia price
4. "NPS Leedu" - Lithuania price
5. "NPS Soome" - Finland price
6. "NPS Eesti" - Estonia price (used by the system)

Example:

```csv
"Ajatempel (UTC)";"Kuupäev (Eesti aeg)";"NPS Läti";"NPS Leedu";"NPS Soome";"NPS Eesti"
"1704060000";"01.01.2024 00:00";"40,01";"40,01";"40,01";"40,01"
```

### Weather Data

The application automatically fetches weather data for Tallinn, Estonia from the Open Meteo Historical Weather API. The
weather data sync process:

- Runs automatically every minute
- Fetches data for dates with electricity prices but missing weather data
- Stores daily average temperature measurements

## Database Schema

### electricity_price

| Column      | Type        | Constraints        |
|-------------|-------------|--------------------|
| id          | BIGINT      | PK, AUTO INCREMENT |
| recorded_at | TIMESTAMPTZ | NOT NULL           |
| price       | DOUBLE      | NOT NULL           |
| country     | VARCHAR(2)  | NOT NULL           |

Unique constraint: `un_recorded_at_country` on (recorded_at, country)
Index: `idx_recorded_at_country` on (recorded_at, country)

### weather_data

| Column              | Type   | Constraints        |
|---------------------|--------|--------------------|
| id                  | BIGINT | PK, AUTO INCREMENT |
| date                | DATE   | NOT NULL, UNIQUE   |
| average_temperature | DOUBLE | NOT NULL           |

Unique constraint: `uc_weather_data_date` on (date)

## Development

### Building and Testing

```bash
# Build the application
mvn clean compile

# Run tests (includes integration tests)
mvn test

# Build Docker image
mvn clean package -DskipTests
docker build -t electricity-price-aggregator .
```

## Configuration

Key application settings in `application.yml`:

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
  datasource:
    url: jdbc:postgresql://localhost:5432/electricity_db
    username: postgres
    password: postgres
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Follow existing code structure and package organization
4. Include tests for new features
5. Update documentation as needed
6. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
