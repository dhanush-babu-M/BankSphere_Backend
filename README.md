# BankSphere Backend

Enterprise Banking System REST API

![Java 21](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot 3.3](https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen.svg)

## Description
BankSphere is a comprehensive, scalable, and secure backend REST API for a modern banking system. Built with Spring Boot 3.3.x and Java 21, it leverages cutting-edge enterprise features including Spring Security with JWT, Spring Data JPA with PostgreSQL, and automated database migrations using Flyway.

## Features
- Complete Authentication and Authorization
- Customer Profile Management
- Accounts and Balances
- Transactions and Transfers
- Loan Processing
- Credit and Debit Card Management
- Fixed Deposits
- Notifications (Email/SMS templates)
- Audit Logging
- PDF Statements
- Advanced Rate Limiting and Caching

## Tech Stack
| Component | Technology |
|---|---|
| Core Framework | Spring Boot 3.3.x |
| Language | Java 21 |
| Database | PostgreSQL |
| Database Migration | Flyway |
| Security | Spring Security, JWT (0.12.3) |
| Mapping | MapStruct (1.5.5.Final) |
| Documentation | SpringDoc OpenAPI (2.3.0) |
| Caching | Caffeine |
| PDF Generation | iText7 |
| Build Tool | Maven |

## Prerequisites
- Java 21
- PostgreSQL
- Maven 3.9+

## Quick Start
1. Clone the repository
2. Ensure PostgreSQL is running and update `application-dev.yml` with your database credentials.
3. Build the project: `./mvnw clean install`
4. Run the application: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`

## API Documentation
Once running, the API documentation is available at:
`http://localhost:8080/api/v1/swagger-ui.html`
