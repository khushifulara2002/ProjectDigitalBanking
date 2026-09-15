# 🏛️ Digital Banking Management System

![Java 21](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot 3.2.5](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)
![Spring Security 6](https://img.shields.io/badge/Spring%20Security-6.0-green.svg)
![MySQL 8.0](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![Angular 17+](https://img.shields.io/badge/Angular-17%2B-red.svg)
![License](https://img.shields.io/badge/License-MIT-purple.svg)

A production-grade, enterprise Full-Stack Digital Banking application engineered to deliver atomic financial transactions, real-time payee validation, administrative oversight, and immutable security audit trails. 

Built with a **Spring Boot 3** REST API backend and a responsive **Angular 17+** Single Page Application (SPA).

---

## 📸 Key Application Capabilities

### 👤 Customer Features
* **Dual-Role Authentication Portal**: Interactive login tab switching between **Customer Login** and **Admin Portal**.
* **Account Management**: Open Savings or Checking accounts, monitor live balances, and deposit cash (`💰 Add Money`).
* **Instant Money Transfer with Live Payee Auto-Lookup**: As soon as a customer types a recipient account number, the system queries the backend REST API and displays a verified badge (`👤 Verified Payee: Shreya Verma`).
* **Saved Beneficiaries Contact Book**: Save, select, and manage frequently transferred contacts stored in MySQL.
* **Account Statements & PDF Export**: Paginated transaction audit ledger with strict type filtering (`DEPOSIT`, `WITHDRAWAL`, `TRANSFER`) and printable **AetherBank PDF Statement Export**.
* **Profile Settings & Security Verification**: Edit profile information with mandatory **Current Password Verification Modal** before persisting changes to MySQL.
* **Account Freeze Transparency**: If an account is frozen by an admin, the customer immediately sees a red notice: `🛑 BLOCKED BY BANK: [Reason]`.

### 🛡️ Administrative Oversight (`ROLE_ADMIN`)
* **System Liquidity Metrics**: Real-time stats on total registered customers, total active bank accounts, and total system liquidity (₹).
* **Customer Account Oversight**: View all customer accounts, filter by user email or account number, and execute **`🔒 Freeze Account`** or **`🔓 Unblock Account`** actions with mandatory administrative reason logging.
* **Security Audit Trail**: Immutable logging of administrative actions stored in the `audit_logs` table.

---

## 🛠️ Technology Stack

### **Backend (`/backend`)**
* **Core Framework**: Java 21, Spring Boot 3.2.5
* **Security & Auth**: Spring Security 6, JJWT (0.12.5), Custom `JwtAuthenticationFilter`, BCrypt Password Hashing
* **Database & Data Access**: MySQL 8.0, Spring Data JPA, Hibernate ORM, Custom JPQL Queries
* **API Documentation**: Springdoc OpenAPI 3 (Swagger UI)
* **Testing & Quality**: JUnit 5, Mockito, Jakarta Bean Validation

### **Frontend (`/frontend`)**
* **Core Framework**: Angular 17+ (TypeScript)
* **State & Async Management**: RxJS (`BehaviorSubject`, `Observables`), HttpClient Interceptors (`JwtInterceptor`)
* **Routing & Security**: Angular Router, Route Guards (`AuthGuard`, `AdminGuard`)
* **UI Design System**: Inter Typography, Glassmorphic Header, Card Micro-animations, Responsive Flex Grid

---

## 🏗️ Architecture & Security Highlights

1. **N-Tier Architecture**: Strict layer separation across Controllers, Services, Repositories, Entities, and DTOs.
2. **ACID `@Transactional` Integrity**: Fund transfers execute atomically across source and target accounts within isolated DB transactions.
3. **Stateless Bearer JWT Flow**: Automated token injection via Angular `HttpInterceptor` and backend stateless filter chain.
4. **Soft-Close Safeguard**: Accounts with transaction ledger history transition to `AccountStatus.CLOSED` status rather than hard deletion, preserving audit logs.

---

## 🚀 Getting Started

### Prerequisites
* **Java Development Kit**: JDK 21+
* **Node.js & npm**: Node v18+
* **Database**: MySQL 8.0 Server running on `localhost:3306`

---

### 1. Database Setup

Open MySQL Workbench or MySQL CLI and run:

```sql
CREATE DATABASE digital_bank_db;
```

---

### 2. Backend Setup (Spring Boot)

```bash
# Navigate to backend directory
cd backend

# Build and run application
mvn spring-boot:run
```

> **REST API Server**: `http://localhost:8080`  
> **Swagger API Documentation**: `http://localhost:8080/swagger-ui/index.html`

---

### 3. Frontend Setup (Angular)

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start Angular local development server
npm start
```

> **Web Application UI**: `http://localhost:4200`

---

## 🔑 Default Seed Admin Credentials

To log in to the **Admin Portal**:
* **Email**: `superadmin@digitalbank.com`
* **Password**: `Admin@2002`

---

## 📄 License
This project is open-source under the [MIT License](LICENSE).
