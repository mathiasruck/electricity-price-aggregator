
# 📅 Development Plan with Technologies  
**Weather and Electricity Price Aggregator** – Hybrid DDD + Layered Architecture

---
## Critical
- Use **Java 17+** 
- Don't use spring-boot-starter-webflux
- USE **Spring Web** (spring-boot-starter-web)
- Use Maven
- Use **PostgreSQL**
- The project root mst be com.mathias.electricity-price-aggregator


## 🧱 PHASE 1: Project Setup & Scaffolding

**Objective**: Prepare the foundational structure, dependencies, and configuration.

### ✅ Tasks:
| Task | Technology |
|------|------------|
| Spring Boot project setup | **Spring Initializr**, **Java 17+, Maven** |
| Dependency management | **Maven** |
| CSV parsing library | **Apache Commons CSV** |
| REST API setup | **Spring Web** |
| API documentation | **springdoc-openapi (Swagger)** |
| Integration testing | **JUnit 5**, **Testcontainers** |
| Docker setup | **Docker**, **docker-compose** |

---

## 🌐 PHASE 2: Define the Domain Model

**Objective**: Implement core business models and domain logic.

### ✅ Tasks:
| Task | Technology / Tool |
|------|--------------------|
| Define Entities (POJOs) | **Plain Java classes** |
| Value Objects | **Immutable Java objects** |
| Repositories (domain interfaces) | **Java interfaces**, no Spring dependency |

---

## ⚙️ PHASE 3: Persistence Layer (Infrastructure)

**Objective**: Implement database access with JPA and connect to domain repositories.

### ✅ Tasks:
| Task | Technology |
|------|------------|
| JPA Entity mappings | **Jakarta Persistence (JPA)** |
| ORM Framework | **Spring Data JPA** |
| Database | **PostgreSQL** |
| Configuration | `application.yml` or `application.properties` |
| Docker DB container | **PostgreSQL in docker-compose** |

---

## 📥 PHASE 4: CSV Upload for Price Data

**Objective**: Parse and persist electricity price data from CSV upload.

### ✅ Tasks:
| Task | Technology |
|------|------------|
| CSV parsing | **OpenCSV** or **Apache Commons CSV** |
| File upload REST API | **Spring MVC** (`@RequestParam MultipartFile`) |
| Upsert logic | **Spring Data JPA** (`save`, `@Query`, or native SQL) |
| Application service | Pure Java with Spring `@Service` annotation |

---

## ☁️ PHASE 5: Weather Data Fetching (Open Meteo API)

**Objective**: Fetch hourly temperature data and calculate daily averages.

### ✅ Tasks:
| Task | Technology |
|------|------------|
| HTTP client | **Spring WebClient** or **RestTemplate** |
| JSON parsing | **Jackson** (default in Spring Boot) |
| Scheduler | **Spring @Scheduled** annotation |
| API consumption | **Open Meteo Historical Weather API** |
| Application logic | Pure Java in `WeatherSyncService` |
| Persistence | Via domain service calling Spring Data JPA repository |

---

## 🔎 PHASE 6: Aggregated Query API

**Objective**: Provide an API to return combined daily electricity prices and temperatures.

### ✅ Tasks:
| Task | Technology |
|------|------------|
| Aggregation logic | **Java Streams**, custom query logic |
| DTO Mapping | Java DTOs or **MapStruct** (optional) |
| REST Controller | **Spring MVC** (`@GetMapping`) |
| Data join (if needed) | **Spring Data JPA**, or manual in service layer |

---

## 🧪 PHASE 7: Testing and Documentation

**Objective**: Ensure quality, correctness, and usability.

### ✅ Tasks:
| Task | Technology |
|------|------------|
| Unit testing | **JUnit 5**, **Mockito** |
| Integration testing | **Testcontainers**, **Spring Boot Test** |
| API testing | **MockMvc**, **RestAssured** (optional) |
| API documentation | **Swagger UI** (via springdoc-openapi) |

---

## 📦 Optional Bonus: CI/CD or Deployment

**Objective**: Polish the project for real-world readiness.

### ✅ Tasks:
| Task | Technology |
|------|------------|
| CI Pipeline | **GitHub Actions**, **GitLab CI**, or **Jenkins** |
| Dockerfile for backend | **Docker**, base image: `openjdk:17-alpine` |
| Local orchestration | **docker-compose.yml** |
| Deployment target (optional) | **Heroku**, **Fly.io**, **Render**, or **AWS EC2** |

---

## ✅ Summary Timeline with Technology Mapping

| Phase                         | Estimate | Key Technologies |
|------------------------------|----------|------------------|
| Project Setup                | 0.5 day  | Spring Boot, Maven, Docker |
| Domain Model                 | 0.5 day  | Plain Java, DDD patterns |
| Persistence Layer            | 0.5 day  | JPA, Spring Data, PostgreSQL |
| CSV Upload Feature           | 1 day    | Spring MVC, OpenCSV, JPA |
| Weather API Sync             | 1 day    | WebClient, Scheduler, JSON |
| Aggregation API              | 0.5 day  | Spring MVC, DTOs |
| Testing & Documentation      | 0.5 day  | JUnit, Mockito, Testcontainers, Swagger |
