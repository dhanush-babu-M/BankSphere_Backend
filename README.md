<<<<<<< HEAD
# 🏦 BankSphere Backend — Enterprise Banking REST API
======= 
# BankSphere Backend(https://banksphere-backend-tj9y.onrender.com)
>>>>>>> 715a6ce871ebcec8b19382141a2064747b533c41

![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg?style=flat-square&logo=openjdk)
![Spring Boot 3.3.4](https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen.svg?style=flat-square&logo=springboot)
![Spring Security 6](https://img.shields.io/badge/Security-Spring_Security_6-blue.svg?style=flat-square&logo=springsecurity)
![Database](https://img.shields.io/badge/Database-MySQL_8.0_%2F_TiDB_Cloud-00758F.svg?style=flat-square&logo=mysql)
![Flyway](https://img.shields.io/badge/Migration-Flyway-CC0202.svg?style=flat-square&logo=flyway)
![Tests](https://img.shields.io/badge/Tests-46%2F46%20Passed%20(100%25)-success.svg?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-purple.svg?style=flat-square)

---

## 📋 Table of Contents
1. [Project Overview](#-1-project-overview)
2. [Complete Technology Stack & Languages Used](#-2-complete-technology-stack--languages-used)
3. [The 6 Pillars: Why BankSphere is an Enterprise-Grade Backend](#-3-the-6-pillars-why-banksphere-is-an-enterprise-grade-backend)
4. [Total Project Wireframe & Architecture Blueprint](#-4-total-project-wireframe--architecture-blueprint)
   - [System Architecture Topology](#system-architecture-topology)
   - [Double-Entry Accounting Ledger Flow](#double-entry-accounting-ledger-flow)
   - [Client UI Screen Wireframes](#client-ui-screen-wireframes)
   - [Database Schema ERD](#database-schema-erd)
5. [🎓 Technical Interview Masterclass ("What You Should Tell the Interviewer")](#-5-technical-interview-masterclass-what-you-should-tell-the-interviewer)
   - [The 60-Second Elevator Pitch](#the-60-second-elevator-pitch)
   - [Top 7 Deep-Dive Technical Questions & Exact Answers](#top-7-deep-dive-technical-questions--exact-answers)
   - [High-Impact Resume Bullet Points](#high-impact-resume-bullet-points)
6. [How to Run, Test, and Debug in VS Code](#-6-how-to-run-test-and-debug-in-vs-code)
7. [API Endpoints Reference](#-7-api-endpoints-reference)

---

## 🌟 1. Project Overview

**BankSphere** is a high-performance, secure, and production-grade REST API backend designed for a modern retail banking institution. Built with **Spring Boot 3.3.4** and **Java 17 (LTS)**, it solves real-world financial engineering problems including **atomic double-entry ledger bookkeeping**, **race condition prevention during concurrent balance deductions**, **stateless JWT authentication with refresh token rotation**, **versioned database schema migrations**, and **dynamic loan amortization calculations**.

Rather than premature microservices complexity that introduces network latency and distributed transaction failures for single teams, BankSphere is architected as a clean **Modular Monolith**—domain modules are separated into clean bounded contexts (`auth`, `account`, `transaction`, `loan`, `customer`, `card`) while sharing an enterprise cross-cutting core.

---

## 💻 2. Complete Technology Stack & Languages Used

| Category | Technology / Language | Version / Specification | Role in BankSphere |
|---|---|---|---|
| **Primary Language** | **Java** | `17` (LTS) / `21` compatible | Core business logic, domain services, security filters, DTOs, and test suites (254+ source files). |
| **Framework** | **Spring Boot** | `3.3.4` (LTS) | Dependency injection, MVC routing, auto-configuration, lifecycle management. |
| **Data Persistence** | **Spring Data JPA / Hibernate** | `6.5.3.Final` | Object-Relational Mapping (ORM) across 31 JPA repository interfaces. |
| **Database & SQL** | **MySQL 8.0 & ANSI SQL** | `8.0` Local / **TiDB Cloud** Serverless | Relational database, foreign key integrity, composite search indexes. |
| **Schema Migrations** | **Flyway** | `Flyway Core + MySQL` | Versioned, audited DDL schema migrations (`V1` init, `V2` seed, `V3` indexes). |
| **Security & Auth** | **Spring Security 6 + JJWT** | `io.jsonwebtoken: 0.12.3` | Stateless authorization filter, HMAC-SHA256 tokens, Refresh Token Rotation, BCrypt. |
| **Mapping** | **MapStruct** | `1.5.5.Final` | Compile-time type-safe entity $\leftrightarrow$ DTO conversions without reflection overhead. |
| **In-Memory Caching** | **Caffeine Cache** | `v3.x` | L1 configuration caching (500 max entries, 10-minute TTL). |
| **Documentation** | **SpringDoc OpenAPI (Swagger 3)** | `2.3.0` | Interactive OpenAPI 3.0 documentation UI at `/api/v1/swagger-ui/index.html`. |
| **PDF Generation** | **iText7 Core** | `7.2.5` | Programmatic financial statement generation with customer branding. |
| **Templating** | **HTML5 + Thymeleaf 3** | Standard HTML5 | Transaction email notification templates and PDF layout designs. |
| **Build Tool & Config** | **Maven (XML) & YAML** | Maven 3.9+, YAML 1.2 | `pom.xml` dependency management and hierarchical `application-*.yml` configurations. |
| **Containerization** | **Docker & Docker Compose** | Dockerfile multi-stage | Reproducible runtime packaging (Eclipse Temurin JRE 17) and orchestration. |
| **Automated Testing** | **JUnit 5 + Mockito + MockMvc + H2** | Jupiter 5.10 | 46 real automated tests: Unit, WebMvc controller slices, and JPA context tests. |

---

## 🛡️ 3. The 6 Pillars: Why BankSphere is an Enterprise-Grade Backend

| Amateur / Beginner Project | BankSphere Production Backend |
|---|---|
| Mutates a single `balance` column via `UPDATE` | **Double-Entry Bookkeeping**: Every transfer generates immutable `DEBIT` and `CREDIT` ledger records. |
| Uses `double` or `float` for currency | **`BigDecimal` with `RoundingMode.HALF_UP`**: Zero floating-point representation errors ($0.1 + 0.2 \neq 0.3$). |
| Static JWTs or basic sessions | **Stateless JWT with Refresh Token Rotation** & SHA-256 hashed password reset tokens. |
| Relies on Hibernate's `ddl-auto: update` | **Flyway Version-Controlled Migrations**: Audited SQL scripts (`V1`, `V2`, `V3`) committed to Git. |
| `try-catch` blocks inside controllers | Centralized **`@RestControllerAdvice`**: Produces RFC-7807 compliant standardized error JSON. |
| Zero tests or dummy `assertTrue(true)` | **46 Real Automated Tests**: 100% green passing across business logic, controllers, and security. |

### 1. Financial & Transaction Integrity (Double-Entry Ledger)
* Every fund transfer runs inside an atomic Spring `@Transactional` boundary.
* A parent `Transaction` record is written, accompanied by **two balancing `LedgerEntry` records**:
  1. A **`DEBIT`** entry against the sender's account with General Ledger code `10001`.
  2. A **`CREDIT`** entry against the recipient's account with General Ledger code `20001`.
* The fundamental accounting equation is preserved: $\sum \text{Debit} - \sum \text{Credit} = 0$.

### 2. Defense-in-Depth Security Architecture
* **Access Tokens (1 hour):** Stateless, signed with HMAC-SHA256, validated in-memory without hitting the database per request.
* **Refresh Token Rotation (7 days):** When a client requests a new access token, the backend validates the refresh token, **immediately revokes it**, and issues a new pair. If a stolen refresh token is replayed, the system detects the compromise and invalidates all user sessions.
* **Hashed Password Reset Tokens:** Raw UUIDs are emailed to the user, but only their **SHA-256 cryptographic hash** is stored in the database. Even during a full database leak, attackers cannot reset user passwords.
* **Role-Based Access Control (RBAC):** Method security via `@PreAuthorize("hasRole('ADMIN')")`.

### 3. Database Performance & Schema Migrations
* **Flyway Migration Engine:** Eliminates schema drift between dev, test, and production environments.
* **HikariCP Connection Pool:** Optimized parameters (`maximum-pool-size: 10`, `connection-timeout: 30000ms`, `max-lifetime: 1800000ms`) to protect against connection starvation.
* **Composite Indexes:** Added on high-cardinality search fields `(account_number, reference_number, created_at)`.

### 4. Resilient Centralized Error Handling
* Domain exceptions (`ResourceNotFoundException`, `InsufficientFundsException`, `DuplicateResourceException`, `AccountLockedException`) are intercepted by [`GlobalExceptionHandler`](src/main/java/com/banksphere/core/exception/GlobalExceptionHandler.java).
* Emits standardized RFC-7807 error responses:
  ```json
  {
    "timestamp": "2026-09-04T13:28:38",
    "status": 404,
    "errorCode": "RES_001",
    "message": "Account not found with accountNumber : NON_EXISTENT",
    "path": "/api/v1/accounts/NON_EXISTENT",
    "errors": null
  }
  ```

### 5. Distributed Tracing & Observability
* **Correlation-ID via MDC:** `RequestLoggingInterceptor` assigns a unique `UUID` to every incoming HTTP request and places it into SLF4J's Mapped Diagnostic Context (MDC), ensuring all logs for a single request can be traced in Datadog/ELK.
* **Spring Boot Actuator:** Exposes `/actuator/health` and `/actuator/metrics`. Transient mail health checks are isolated to prevent SMTP failures from failing cloud deployment health checks.

### 6. Automated Testing Suite (46 Tests Verified 100% Green)
* **Unit Tests (Mockito):** Token generation & validation, account number format & uniqueness, balance credit/debit, auth rotation, customer registration & KYC, loan EMI formulas.
* **WebMvc Slice Tests (MockMvc):** Controller status codes (200 OK, 404 Not Found, 422 Unprocessable Entity) and JSON body validation.
* **Integration Tests:** Full Spring context verification against in-memory H2.

---

## 🏛️ 4. Total Project Wireframe & Architecture Blueprint

### System Architecture Topology

```text
+===========================================================================================+
|                                1. CONSUMERS & CLIENTS                                     |
|  [ Web React/Vue App ]      [ Mobile iOS/Android ]      [ Admin / Teller ]     [ Swagger ]|
+=============================================+=============================================+
                                              | HTTPS / JSON (Bearer JWT)
                                              v
+===========================================================================================+
|                     2. CROSS-CUTTING CORE & SECURITY GATEWAY                              |
|                                                                                           |
|  +---------------------------+   +----------------------------+   +--------------------+  |
|  |  JwtAuthenticationFilter  |-->|  RequestLoggingInterceptor |-->|   Rate Limiting    |  |
|  | (HMAC-SHA256 Token Check) |   | (UUID Correlation-ID / MDC)|   |   (60 req/min)     |  |
|  +---------------------------+   +----------------------------+   +--------------------+  |
|                                                                                           |
|  +-------------------------------------------------------------------------------------+  |
|  |  GlobalExceptionHandler (@RestControllerAdvice: 404, 422, 409, 423 RFC-7807 Errors) |  |
|  +-------------------------------------------------------------------------------------+  |
+=============================================+=============================================+
                                              | Route Matching & DTO Validation
                                              v
+===========================================================================================+
|                     3. MODULAR BUSINESS DOMAINS (Services & Mappers)                      |
|                                                                                           |
|  +--------------------+  +--------------------+  +--------------------+  +--------------+ |
|  |    Auth Module     |  |   Account Module   |  | Transaction Module |  | Loan Module  | |
|  | • Login / MFA      |  | • Available Bal    |  | • Double-Entry     |  | • EMI Calc   | |
|  | • Refresh Rotation |  | • Hold Reservation |  | • Debit/Credit GL  |  | • Schedule   | |
|  | • SHA-256 Reset    |  | • Balance History  |  | • Reversals        |  | • Approval   | |
|  +--------------------+  +--------------------+  +--------------------+  +--------------+ |
|                                                                                           |
|  +--------------------+  +--------------------+  +--------------------+  +--------------+ |
|  |  Customer Module   |  |    Cards Module    |  |  Reports & Export  |  | Admin Audit  | |
|  | • KYC Verification |  | • Debit/Credit PIN |  | • iText7 PDF       |  | • AOP Aspect | |
|  | • Profile Updating |  | • Limits Management|  | • Thymeleaf Email  |  | • Lock/Freeze| |
|  +--------------------+  +--------------------+  +--------------------+  +--------------+ |
+=============================================+=============================================+
                                              | Spring Data JPA / @Transactional
                                              v
+===========================================================================================+
|                       4. DATA PERSISTENCE & INFRASTRUCTURE TIER                           |
|                                                                                           |
|  +------------------------+  +----------------------------+  +--------------------------+ |
|  |   Flyway Migrations    |  |    HikariCP Connection     |  |      Caffeine Cache      | |
|  | (V1__init -> V3__index)|  | (10 max connections, 30s)  |  | (500 max, 10 min TTL)    | |
|  +------------------------+  +----------------------------+  +--------------------------+ |
|                                              |                                            |
|                                              v                                            |
|                      [( MySQL 8.0 / TiDB Serverless Cloud Database )]                     |
+===========================================================================================+
```

---

### Double-Entry Accounting Ledger Flow

```text
Client Transfer Request: Deduct ₹ 15,000 from Alice (10001) and Credit Bob (20001)
----------------------------------------------------------------------------------
Step 1: Check Alice's Account Status == ACTIVE
Step 2: Check Alice's Available Balance >= ₹ 15,000.00
Step 3: Atomic Mutation within @Transactional boundary:
        • Alice Balance: ₹ 1,45,280.50 -> ₹ 1,30,280.50
        • Bob Balance:   ₹   5,000.00 -> ₹  20,000.00
Step 4: Write Parent Transaction Entity (Reference: TXN2026090412345678)
Step 5: Write Double Ledger Journal:
        +------------------------------------------------------------------------+
        | Entry 1: DEBIT  | GL Account: 10001 | Account: Alice | - ₹ 15,000.00   |
        | Entry 2: CREDIT | GL Account: 20001 | Account: Bob   | + ₹ 15,000.00   |
        +------------------------------------------------------------------------+
        Net Accounting Invariant: Sum(Debits) - Sum(Credits) = ₹ 0.00 (Balanced)
```

---

### Client UI Screen Wireframes

#### Screen 1: Customer Online Banking Dashboard (`GET /accounts/{number}/balance`)
```text
+-----------------------------------------------------------------------------------+
|  🏦 BankSphere Online Banking                🔔 Notifications (2)   👤 John Doe |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|   +---------------------------------------------+   +-------------------------+   |
|   | SAVINGS ACCOUNT: BSP2026123456789           |   | Quick Services          |   |
|   |                                             |   |                         |   |
|   | Current Balance:   ₹ 1,45,280.50            |   | • [ Apply for Loan ]    |   |
|   | Available Balance: ₹ 1,40,280.50            |   | • [ Fixed Deposit (FD) ]|   |
|   | On Hold:           ₹    5,000.00            |   | • [ Statement (PDF) ]   |   |
|   | Status:            [ ACTIVE ]               |   | • [ KYC: ✓ VERIFIED ]   |   |
|   |                                             |   +-------------------------+   |
|   | [ 💸 Quick Transfer ]   [ 📑 Download PDF ] |                                 |
|   +---------------------------------------------+                                 |
|                                                                                   |
|   Recent Transaction History (Auto-balanced Double Entry Ledger)                  |
|   +------------+--------------------+-------------------------+----------+--------+
|   | Date       | Reference #        | Description             | Type     | Amount |
|   +------------+--------------------+-------------------------+----------+--------+
|   | 2026-09-04 | TXN202609041234    | Salary Credit           | CREDIT   |+₹85,000|
|   | 2026-09-03 | TXN202609038765    | Transfer to BSP2026002  | DEBIT    |-₹12,000|
|   +------------+--------------------+-------------------------+----------+--------+
+-----------------------------------------------------------------------------------+
```

#### Screen 2: Loan Application & EMI Calculator (`GET /loans/calculate-emi`)
```text
+-----------------------------------------------------------------------------------+
|  📊 Personal Loan Calculator & Origination                                         |
+-----------------------------------------------------------------------------------+
|  Principal (P):    [ ₹ 5,00,000                                               ]   |
|  Interest Rate:    [ 10.50% p.a.                                              ]   |
|  Tenure (Months):  [ 24 Months                                                ]   |
|                                                                                   |
|  +-----------------------------------------------------------------------------+  |
|  | Calculated Monthly EMI:  ₹ 23,191.00                                        |  |
|  | Total Repayment:         ₹ 5,56,584.00  (Principal: ₹5,00,000 | Int: ₹56,584)|  |
|  +-----------------------------------------------------------------------------+  |
|                                                                                   |
|  Amortization Schedule Breakdown:                                                 |
|  +-------+------------+---------------+--------------+-------------------------+  |
|  | Month | Due Date   | Principal     | Interest     | Outstanding Principal   |  |
|  +-------+------------+---------------+--------------+-------------------------+  |
|  | 01    | 2026-10-05 | ₹ 18,816.00   | ₹ 4,375.00   | ₹ 4,81,184.00           |  |
|  | 02    | 2026-11-05 | ₹ 18,981.00   | ₹ 4,210.00   | ₹ 4,62,203.00           |  |
|  | 03    | 2026-12-05 | ₹ 19,147.00   | ₹ 4,044.00   | ₹ 4,43,056.00           |  |
|  +-------+------------+---------------+--------------+-------------------------+  |
|                                                                                   |
|             [ Recalculate ]                    [ 📝 Submit Application ]          |
+-----------------------------------------------------------------------------------+
```

---

### Database Schema ERD

```text
  +----------------------+             +--------------------------+
  |        users         |             |      refresh_tokens      |
  +----------------------+             +--------------------------+
  | PK  id (UUID)        | 1 ------- * | PK  id (UUID)            |
  | UK  username         |             | FK  user_id (UUID)       |
  | UK  email            |             |     token (VARCHAR)      |
  |     password (BCrypt)|             |     revoked (BOOLEAN)    |
  |     account_locked   |             |     expiry_date (INSTANT)|
  +----------+-----------+             +--------------------------+
             | 1
             |
             | 1
  +----------v-----------+             +--------------------------+
  |      customers       |             |         accounts         |
  +----------------------+             +--------------------------+
  | PK  id (UUID)        | 1 ------- * | PK  id (UUID)            |
  | UK  customer_id      |             | UK  account_number (16)  |
  | UK  pan_number       |             |     balance (DECIMAL)    |
  |     kyc_status       |             |     available_bal (DEC)  |
  +----------------------+             |     status (ACTIVE, ...) |
                                       +------------+-------------+
                                                    | 1
                       +----------------------------+----------------------------+
                       | *                                                       | *
          +------------v-------------+                              +------------v-------------+
          |       transactions       |                              |      account_holds       |
          +--------------------------+                              +--------------------------+
          | PK  id (UUID)            |                              | PK  id (UUID)            |
          | UK  reference_number     |                              | FK  account_id (UUID)    |
          | FK  account_id (UUID)    |                              |     hold_amount (DECIMAL)|
          |     amount (DECIMAL)     |                              |     released (BOOLEAN)   |
          |     status (COMPLETED)   |                              +--------------------------+
          +------------+-------------+
                       | 1
                       |
                       | * (Exactly 2 per Transfer)
          +------------v-------------+
          |      ledger_entries      |
          +--------------------------+
          | PK  id (UUID)            |
          | FK  transaction_id (UUID)|
          | FK  account_id (UUID)    |
          |     entry_type           | --> 'DEBIT' or 'CREDIT'
          |     gl_account_code      | --> '10001' or '20001'
          |     amount (DECIMAL)     |
          |     running_balance (DEC)|
          +--------------------------+
```

---

## 🎓 5. Technical Interview Masterclass ("What You Should Tell the Interviewer")

### The 60-Second Elevator Pitch
> *"I built **BankSphere**, an enterprise-grade banking backend in Java 17 and Spring Boot 3.3.
> 
> Rather than a standard CRUD application, I engineered it around financial-grade patterns:
> - **Financial Reliability:** I designed a **Double-Entry Bookkeeping Ledger** where every transfer writes atomic debit and credit entries inside `@Transactional` boundaries, using `BigDecimal` to eliminate floating-point rounding errors.
> - **Security:** I implemented **Stateless JWT with Refresh Token Rotation** to protect against token hijacking, and hashed password reset tokens with SHA-256 so raw credentials are never stored in the database.
> - **Production Readiness:** I integrated **Flyway database migrations** to ensure zero schema drift, tuned **HikariCP connection pooling**, configured MDC correlation logging for distributed tracing, and validated the entire application with a **46-test automated suite** covering unit, controller slice, and context integration tests."*

---

### Top 7 Deep-Dive Technical Questions & Exact Answers

#### Q1: "How do you handle money transfers to ensure money is never lost if the server crashes midway?"
**Your Answer:**
> *"I enforce three levels of data protection:
> 1. **Declarative Transactions (`@Transactional`):** The debit and credit operations are bound to a single transaction context. If the server loses power or a database constraint fails midway, the database automatically rolls back all changes via Write-Ahead Logging (WAL).
> 2. **Double-Entry Bookkeeping:** Instead of simply running an `UPDATE balance` query, the system writes an immutable `Transaction` record and two balancing `LedgerEntry` records (DEBIT on sender, CREDIT on receiver). This creates an indelible audit trail.
> 3. **Mathematical Precision:** All monetary amounts use `java.math.BigDecimal` with explicit scale to avoid IEEE-754 binary floating-point rounding inaccuracies."*

#### Q2: "How would you prevent race conditions if a user initiates two simultaneous transfers from the same account?"
**Your Answer:**
> *"In high-concurrency environments, two concurrent requests could read the same initial balance, both pass the balance check, and cause an overdraft.
> In BankSphere, we can handle this via:
> - **Pessimistic Locking (`PESSIMISTIC_WRITE`):** In Spring Data JPA, using `@Lock(LockModeType.PESSIMISTIC_WRITE)` executes a `SELECT ... FOR UPDATE` on the account record, making subsequent requests wait until the first commits or rolls back.
> - **Optimistic Locking (`@Version`):** Adding a version column on the `Account` entity. If a concurrent transaction commits first, Hibernate throws an `OptimisticLockException`, which our centralized `GlobalExceptionHandler` intercepts to initiate a clean retry."*

#### Q3: "Why did you implement Refresh Token Rotation instead of a single long-lived JWT?"
**Your Answer:**
> *"JWTs are stateless; once signed, they cannot be invalidated without maintaining a server-side blacklist. A long-lived JWT creates a huge attack window if intercepted.
> In BankSphere, access tokens are short-lived (60 minutes). When the client calls `/auth/refresh`:
> 1. The server validates the refresh token in the database.
> 2. It **immediately revokes that token** (`revoked = true`).
> 3. It issues a brand-new access token and a brand-new refresh token.
> If an attacker steals a refresh token and tries to use it after the user has already refreshed, the system detects a revoked token reuse attempt and can revoke all active sessions for that user."*

#### Q4: "Why use Flyway instead of Hibernate's `ddl-auto = update`?"
**Your Answer:**
> *"`ddl-auto = update` is dangerous in production because:
> 1. It only adds columns; it never drops or renames them, causing silent schema drift.
> 2. It cannot track history or execute rollbacks across environments (dev, staging, prod).
> 3. It can cause table lockouts during multi-node rolling deployments.
> Flyway ensures all schema changes are committed to Git as versioned SQL scripts (`V1`, `V2`, `V3`). On startup, Flyway checks the `flyway_schema_history` table and applies only pending migrations in order, guaranteeing 100% schema parity across all machines."*

#### Q5: "How does your Global Exception Handler work, and why not use try-catch in each controller?"
**Your Answer:**
> *"Using try-catch blocks in controllers violates Separation of Concerns and leads to massive code duplication.
> Instead, I use Spring's `@RestControllerAdvice` with `@ExceptionHandler` methods. When any layer (Service, Repository, or Controller) throws a domain exception like `InsufficientFundsException` or `ResourceNotFoundException`, Spring routes it to `GlobalExceptionHandler`.
> The handler logs the error, translates it into a standard HTTP status code (e.g., 404, 422), and wraps it in a consistent `ErrorResponse` object. This guarantees that clients receive predictable JSON contracts even during unhandled system errors."*

#### Q6: "What is the difference between `@SpringBootTest` and `@WebMvcTest`?"
**Your Answer:**
> *"- **`@SpringBootTest`:** Starts the entire Spring `ApplicationContext`, bootstrapping every bean, JPA repository, and configuration. It is an integration test and is slower.
> - **`@WebMvcTest`:** A slice test that loads **only the web layer** (Controllers, ControllerAdvice, Filters, Formatters), omitting `@Service` and `@Repository` beans. Dependencies are mocked using `@MockBean`. It is lightweight, fast, and ideal for testing HTTP status codes, JSON serialization, and request validation without touching the database."*

#### Q7: "How is loan EMI and amortization calculated in BankSphere?"
**Your Answer:**
> *"I implemented the standard financial banking EMI formula:
> $$EMI = \frac{P \times R \times (1+R)^N}{(1+R)^N - 1}$$
> where $P$ is principal, $R$ is monthly rate ($\text{annual rate} / 1200$), and $N$ is tenure in months.
> In `LoanServiceImpl`, upon loan approval, the system generates the EMI and iterates through each month $i \in [1, N]$:
> - $\text{Interest for month} = \text{Remaining Balance} \times R$
> - $\text{Principal for month} = EMI - \text{Interest for month}$
> - $\text{Remaining Balance} = \text{Remaining Balance} - \text{Principal for month}$
> Each schedule installment is persisted to `loan_schedules` so both the bank and customer have a deterministic payment roadmap."*

---

### High-Impact Resume Bullet Points
Copy and paste these bullet points directly onto your resume:

- **Engineered BankSphere**, a production-grade enterprise banking REST API using **Java 17, Spring Boot 3.3, Spring Data JPA, and Spring Security 6**.
- **Designed a Double-Entry Bookkeeping Ledger** ensuring transactional integrity (`@Transactional`) where transfers generate atomic Debit & Credit entries, handling currency using `BigDecimal` for zero floating-point error.
- **Architected Stateless Authentication** featuring **JWT Refresh Token Rotation**, BCrypt password hashing, and SHA-256 hashed password reset tokens to protect against credential leaks and replay attacks.
- **Optimized Database Layer** with **HikariCP connection pooling** (10 max connections, 30s timeout), composite indexing on high-cardinality search fields, and versioned **Flyway migrations** (`V1` to `V3`).
- **Built End-to-End Testing Suite** with **46 automated tests** using **JUnit 5, Mockito, MockMvc, and H2 in-memory DB**, achieving comprehensive coverage across core financial workflows and security filters.
- **Implemented Distributed Observability** with SLF4J MDC **Correlation IDs** for request tracing, centralized `@RestControllerAdvice` error contracts, and **Spring Boot Actuator** health monitoring.

---

## 🛠️ 6. How to Run, Test, and Debug in VS Code

### Prerequisites
- **Java 17 (or 21)** installed and `JAVA_HOME` configured.
- **MySQL 8.0** (or use the included `docker-compose.yml` / TiDB Cloud instance).

### 1. Terminal Commands
Open terminal (`Ctrl + \``) in VS Code:

- **Run all 46 automated tests:**
  ```powershell
  .\mvnw.cmd test
  ```
- **Package executable production JAR:**
  ```powershell
  .\mvnw.cmd clean package -DskipTests
  ```
- **Run the Spring Boot application:**
  ```powershell
  java -jar target/banksphere-backend-1.0.0.jar --spring.profiles.active=dev
  ```

### 2. VS Code Graphical Test Runner
1. Click the **Testing Flask icon (🧪)** in the left sidebar.
2. Click the top **Play (▶️)** button to run all 46 tests visually.
3. Open any test file (e.g. [`TransactionServiceImplTest.java`](src/test/java/com/banksphere/modules/transaction/service/TransactionServiceImplTest.java)) and click **Run Test** above any `@Test` method.

### 3. VS Code Run & Debug
1. Press `Ctrl + Shift + D` to open the **Run & Debug** panel.
2. Select **`Spring Boot - BankSphereApplication (Test Profile / H2)`** (runs without needing local MySQL started).
3. Press **`F5`** to launch with breakpoints enabled.

### 4. Interactive API Testing in VS Code
Open the included [`api-requests.http`](api-requests.http) file and click **`Send Request`** above any endpoint to test health, login, transfers, and loans directly inside VS Code!

---

## 📡 7. API Endpoints Reference

| Module | Method | Endpoint | Description | Auth Required |
|---|---|---|---|---|
| **Health** | `GET` | `/api/v1/actuator/health` | System health check status | No |
| **Docs** | `GET` | `/api/v1/swagger-ui/index.html` | Interactive OpenAPI 3.0 UI | No |
| **Auth** | `POST` | `/api/v1/auth/register` | Register new user account | No |
| **Auth** | `POST` | `/api/v1/auth/login` | Authenticate & retrieve JWT pair | No |
| **Auth** | `POST` | `/api/v1/auth/refresh` | Rotate refresh token for access token | No |
| **Accounts** | `POST` | `/api/v1/accounts` | Open a new bank account | Yes (Bearer) |
| **Accounts** | `GET` | `/api/v1/accounts/{number}` | Retrieve account details | Yes (Bearer) |
| **Accounts** | `GET` | `/api/v1/accounts/{number}/balance` | Get current, available & hold balance | Yes (Bearer) |
| **Transfers** | `POST` | `/api/v1/transactions/transfer/internal` | Instant transfer with double-entry ledger | Yes (Bearer) |
| **Transfers** | `GET` | `/api/v1/transactions/{reference}` | Get transaction receipt | Yes (Bearer) |
| **Loans** | `GET` | `/api/v1/loans/calculate-emi` | Live mathematical EMI calculator | Yes (Bearer) |
| **Loans** | `POST` | `/api/v1/loans/apply` | Submit loan application | Yes (Bearer) |
| **Customers** | `POST` | `/api/v1/customers` | Onboard customer with KYC data | Yes (Bearer) |
| **Customers** | `POST` | `/api/v1/customers/{id}/kyc` | Submit KYC identity document | Yes (Bearer) |
