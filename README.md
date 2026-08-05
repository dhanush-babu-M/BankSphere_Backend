# BankSphere Backend

Enterprise Banking System REST API

![Java 17+](https://img.shields.io/badge/Java-17%20%2F%2021-orange.svg)
![Spring Boot 3.3](https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen.svg)
![Database](https://img.shields.io/badge/Database-MySQL%20%2F%20TiDB-blue.svg)
![Flyway](https://img.shields.io/badge/Migration-Flyway-red.svg)

---

## 📋 Description
BankSphere is a secure, scalable, and enterprise-grade backend REST API for a modern banking application. Designed with microservices-style domain partitioning (layered design inside a modular monolith structure), it implements core financial workflows including secure transfers, double-entry ledger entries, loan schedules, audit trails, and automated database schema migrations.

---

## 🛠️ Technology Stack & Specifications

| Component | Technology | Version / Specification |
|---|---|---|
| **Core Framework** | Spring Boot | `3.3.4` (LTS) |
| **Language** | Java | `17` / `21` compatibility |
| **Database** | MySQL | `8.0` (Local) / **TiDB Cloud** (Serverless Cloud DB) |
| **Database Migration** | Flyway | Automated versioned migrations (`V1` to `V3`) |
| **Security** | Spring Security | Stateful authorization filters, CORS configurations |
| **JWT Library** | io.jsonwebtoken (JJWT) | `0.12.3` (HS256 signing using raw byte arrays) |
| **Entity Mapping** | MapStruct | `1.5.5.Final` (unmapped properties ignored globally) |
| **Documentation** | SpringDoc OpenAPI | `2.3.0` (Swagger UI integration) |
| **Caching** | Caffeine Cache | 500 max entries, 10-minute time-to-live |
| **Health Checks** | Spring Boot Actuator | Customized indicators with health checks |
| **Build Tool** | Maven | Custom compiler configuration (`release 17`) |

---

## 📁 Folder Structure

```text
BankSphereBackend/
├── 📄 pom.xml                           # Maven project config & plugin definitions
├── 📄 Dockerfile                        # Multi-stage JVM runtime deployment config
├── 📄 docker-compose.yml                # Multi-container orchestration (MySQL + App)
└── src/
    ├── main/
    │   ├── java/com/banksphere/
    │   │   ├── BankSphereApplication.java       # App entry point (Async & Caching enabled)
    │   │   ├── 🔷 core/                         # Cross-Cutting Core Concerns
    │   │   │   ├── config/                      # Cache, Cors, ThreadPool, Jackson configs
    │   │   │   ├── constants/                   # Static final constants & Error codes
    │   │   │   ├── exception/                   # Global exception handler & domain exceptions
    │   │   │   ├── interceptor/                 # Correlation IDs & Rate limit interceptors
    │   │   │   └── security/                    # JWT Auth Filter, custom UserDetailsService
    │   │   └── 🔶 modules/                      # Business Domain Modules
    │   │       ├── account/                     # Account limits & balance management
    │   │       ├── auth/                        # Registration, login, token rotation, resets
    │   │       ├── customer/                    # KYC documents and profile onboarding
    │   │       ├── debitcard/ / creditcard/     # Card transaction logs and billing cycles
    │   │       ├── loan/                        # Loan schedules & amortization calculator
    │   │       ├── payment/                     # Utility merchant payments & gateway logs
    │   │       ├── reports/                     # Statements & financial reporting services
    │   │       └── transaction/                 # Internal, External (NEFT/RTGS), & Wire transfers
    │   └── resources/
    │       ├── application.yml                  # Global defaults & Actuator configurations
    │       ├── application-prod.yml             # Cloud profiles (TiDB integration fallbacks)
    │       ├── db/migration/                    # Versioned schema & seed Flyway migrations
    │       └── templates/                       # Email OTP & PDF statement templates
    └── test/java/com/banksphere/                # Unit & Integration Tests (46 test cases)
```

---

## 🎓 Key Architectural & Interview Talking Points

If you are presenting this project in a technical interview, here are the most important implementation details to focus on:

### 1. Financial Integrity via Double-Entry Ledger
To ensure absolute accounting integrity and prevent race conditions:
* Every fund transfer creates a **`Transaction`** record along with two corresponding **`LedgerEntry`** records (a `DEBIT` on the source account and a `CREDIT` on the destination account).
* Transactions execute inside Spring's `@Transactional` boundaries, ensuring either both sides succeed or the transaction rolls back cleanly.

### 2. Enterprise-Grade Security Architecture
* **JWT Refresh Token Rotation:** Implements security rotation by revoking old refresh tokens whenever a new access token is requested. If a user logs out, resets, or changes their password, all active tokens are bulk-revoked in the database via custom repository queries to mitigate N+1 query overhead.
* **Hashed Password Reset:** Avoids storing raw password reset tokens. Instead, the raw token is sent to the user's email, and only its SHA-256 hash is stored in the database, preventing token leakage in case of a DB breach.
* **XSS & CSRF Filters:** Implements Custom filters to sanitize headers and input parameters against Cross-Site Scripting (XSS) and verify Origin/Referer parameters to guard against CSRF.

### 3. Database Optimization & Cloud Scaling
* **Dynamic Indexing:** Added composite and single-column indexes on high-frequency search fields (`reference_number`, `account_number`, `user_id`, and `created_at`) to optimize read query speeds.
* **TiDB Integration:** Configured to integrate natively with **TiDB Serverless**, resolving standard MySQL driver connection constraints (e.g. SSL connection attributes).
* **Caching & Connection Pooling:** Uses Caffeine Cache to cache configuration values, and utilizes HikariCP with optimized parameters (`maximum-pool-size: 10`, `connection-timeout: 30000`) for connection pooling.

### 4. Resilient Cloud-Ready Design
* **Dynamic Port Allocation:** Reads the active port via `${PORT:8080}`, allowing seamless deployment on cloud hosting providers like Render.com.
* **Isolated Actuator Health Checks:** Disabled the mail health check from the global health check endpoint to prevent SMTP credential mismatches from crashing Render's deployment health checks.

---

## ⚡ Quick Start & Run Instructions

### Prerequisites
- **Java 17** (Ensure `JAVA_HOME` points to JDK 17)
- **MySQL 8.0** / TiDB Cloud instance
- **Maven 3.9+**

### Steps
1. Clone this repository.
2. Edit [`src/main/resources/application.yml`](file:///c:/Users/Poorna%20Chandu%20Kumar/Videos/Career/InProgress_Projects/BankSphereBackend/src/main/resources/application.yml) with your database configurations, or pass them as environment variables.
3. Package the application:
   ```bash
   ./mvnw clean package -DskipTests
   ```
4. Run the application:
   ```bash
   java -jar target/banksphere-backend-1.0.0.jar --spring.profiles.active=dev
   ```
5. Interactive OpenAPI Swagger documentation is available at:
   ```text
   http://localhost:8080/api/v1/swagger-ui/index.html
   ```
