# Product Catalog

A Spring Boot-based product catalog application with PostgreSQL, Flyway migrations and Docker support.

---

## Table of Contents

- [Overview](#overview)
- [Functionality](#functionality)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Database Migrations with Flyway](#database-migrations-with-flyway)
- [Running with Docker](#running-with-docker)
- [Contacts](#contacts)

---

## Overview

This project implements a product catalog service. It uses Spring Boot, Spring Data JPA, PostgreSQL, and Flyway for database migrations. The code is split into modules:
- **product-catalog-common**: shared DTOs, exceptions and enums
- **product-catalog-core**: domain logic, entities, repositories, services
- **product-catalog-app**: the Spring Boot application, config files and Flyway migrations

All modules use Java 17 and Maven. Docker and Docker Compose are used to run the application and PostgreSQL in containers. Flyway migrations live in the `product-catalog-app/src/main/resources/db/migration`.

---

## Functionality

The application provides comprehensive product catalog and price monitoring capabilities:

### User Management & Authentication
- **User Registration**: Support for two types of users - administrators and regular users
- **Profile Management**: Users can edit their personal profiles and account information
- **JWT-based Authentication**: Secure authentication using JSON Web Tokens

### Product & Store Management
- **Product Categories Directory**: Hierarchical categorization system for organizing products
- **Store Directory**: Management of retail locations and trading points
- **Product CRUD Operations**: Add, edit, delete, and view products with full administrative control

### Product Discovery & Search
- **Category-based Browsing**: Browse products organized by categories
- **Advanced Search**: Search products by name, description, and other attributes
- **Filtering System**: Filter products by various criteria (price range, category, store availability)

### Price Management & Monitoring
- **Price History**: Track price changes over time with detailed historical data
- **Dynamic Price Analysis**: Monitor price fluctuations for any product within specified time periods
- **Tabular Price Display**: View price data in structured table format

### Price Comparison & Analytics
- **Multi-store Price Comparison**: Compare prices for the same product across different stores
- **Graphical Price Charts**: Visual representation of price trends using JFreeChart integration
- **Price Dynamics Visualization**: Interactive charts showing price changes over time

### Data Import & Batch Operations
- **Bulk Data Import**: Import product and pricing information in batch mode
- **Multiple Format Support**: Support for JSON
- **Automated Data Processing**: Streamlined import process for large datasets

### Additional Features
- **Role-based Access Control**: Different permissions for administrators and regular users
- **Data Validation**: Comprehensive input validation and error handling
- **RESTful API**: Well-structured REST endpoints for all operations
- **Database Migrations**: Automated schema management with Flyway

---

## Technology Stack

Below are the main technologies, frameworks, and versions used in this project:

- **Java**: 17
- **Spring Boot**: web, data-jpa, security, validation, testing
- **Hibernate / Spring Data JPA** for ORM
- **PostgreSQL** (15.13) database
- **Flyway** (11.9.0) for database migrations
- **Lombok** (1.18.32) for reducing boilerplate
- **MapStruct** (1.5.5.Final) for mapping between entities and DTOs
- **JFreeChart** (1.5.3) for price charts
- **JWT (io.jsonwebtoken 0.12.6)** for authentication tokens
- **Maven** for build and dependency management
- **Docker & Docker Compose** for containerized runtime
- **JUnit 5 / Spring Boot Test** and **Spring Security Test** for automated tests

Maven properties are set in parent POM, for example:
```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

    <!-- Module versions -->
    <app-module.version>1.0-SNAPSHOT</app-module.version>
    <common-module.version>1.0-SNAPSHOT</common-module.version>
    <core-module.version>1.0-SNAPSHOT</core-module.version>

    <!-- Dependency versions -->
    <postgresql.version>42.7.3</postgresql.version>
    <flyway.version>11.9.0</flyway.version>
    <lombok.version>1.18.32</lombok.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <jfreechart.version>1.5.3</jfreechart.version>
    <jwt.version>0.12.6</jwt.version>
</properties>
```

---

## Project Structure

Top-level layout (Maven multi-module):

```
product_price_monitoring/
├── product-catalog-app/                # Spring Boot application, controllers, Flyway scripts
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/...                # Application code
│           └── resources/
│               └── db/migration/       # Flyway SQL files (V1__..., V2__..., etc.)
├── product-catalog-common/             # Shared DTOs, util classes, etc.
│   └── pom.xml
├── product-catalog-core/               # Domain: entities, repositories, services
│   └── pom.xml
├── docker-compose.yml                  # Docker Compose config for app + Postgres
├── .env                                # Environment variables for Docker
├── pom.xml                             # Parent POM listing modules and common settings
└── README.md                           # This file
```

---

## Configuration

1. **Environment variables**: create a `.env` file at project root. Example:

   ```dotenv
   # .env
    POSTGRES_DB=your_db
    POSTGRES_USER=your_user
    POSTGRES_PASSWORD=your_password
    DATASOURCE_URL=jdbc:postgresql://postgres:5432/your_db
    DATASOURCE_DRIVER=org.postgresql.Driver

    JWT_SECRET_KEY=your-secret-key
   ```

2. **Flyway migrations**: place SQL files in `product-catalog-app/src/main/resources/db/migration`, named like:

   ```
   V1__create_tables.sql
   V2__insert_initial_data.sql
   ...
   ```

   Migrations run automatically on app startup (if Flyway is enabled).

---

## Database Migrations with Flyway

* Flyway scripts live in `product-catalog-app/src/main/resources/db/migration`.
* On application start, Flyway scans and applies any pending migrations to the database.
* If you need to reset the database in Docker, remove the Postgres volume so migrations start from scratch (see [Running with Docker](#running-with-docker)).
* To add a new change, create a new file `V<number>__description.sql`.

---

## Running with Docker

1. **Build and start**:

   ```bash
   # Create jar files
   mvn clean install
   # USE sudo ON LINUX
   # Build and run
   docker compose up -d --build
   
   # Stop existing containers
   docker compose down

   # (Optional) Remove existing volume to start fresh:
   docker volume rm <your_project>_pgdata
   # Replace <your_project> with your Docker Compose project name; often folder name in lowercase with underscores.
   # E.g.: product_price_monitoring_pgdata
   ```
2. **Behavior**:

   * The `postgres` container starts with an empty or existing database.
   * On first run (empty volume), Flyway migrations run automatically when the Spring Boot app connects.
   * On subsequent runs (with data), migrations only apply new scripts.
3. **Logs**: check app logs:
   ```bash
   docker-compose logs app
   ```
4. **Access**:

   * API endpoints available at `http://localhost:8080/api/...`
   * Connect to Postgres if needed: `psql -h localhost -p 5432 -U <user> -d <db>`.

---

## Contacts

If you have questions or need support, contact:

* Email: [antonbut48@gmail.com](mailto:antonbut48@gmail.com)