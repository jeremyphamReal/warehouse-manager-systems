# Warehouse Management System

A backend inventory management system built with **Spring Boot**, handling real-time stock in/out transactions with role-based access control, JWT authentication, and safe handling of concurrent stock updates.

> Personal project built to practice backend system design, transactional data integrity, and secure API design.

---

## Overview

This system manages products, categories, and stock transactions (inbound/outbound inventory movements) for a warehouse, with two user roles:

- **Admin** — manages categories, products, and user accounts
- **Staff** — performs stock in/out operations

The core engineering challenge this project solves: **preventing data corruption when multiple users update the same product's inventory at the same time** — a common real-world problem in warehouse and e-commerce systems.

---

## Key Features

- **JWT Authentication & Role-Based Authorization** — login issues a JWT; every protected endpoint verifies the token and role. The identity of the user performing an action (e.g. a stock transaction) is always derived from the verified token, never trusted from client input.
- **Concurrency-Safe Inventory Updates** — a dual-locking strategy:
  - **Pessimistic Locking** on the product row during stock in/out transactions, so concurrent requests on the same product are processed safely, one at a time.
  - **Optimistic Locking (`@Version`)** with automatic retry as a safeguard for other update paths (e.g. an admin editing product details) that don't go through the pessimistic-locked flow.
- **Full Stock Transaction Audit Trail** — every inventory movement records the transaction type (IN/OUT), quantity before and after, timestamp, and the user who performed it.
- **Business Rule Enforcement** — stock-out requests exceeding current inventory are rejected before any data is written.
- **Centralized Exception Handling** — consistent, meaningful HTTP responses (`400` for invalid input, `404` for missing resources, `409` for lock conflicts) instead of raw server errors.
- **Unit Tested Business Logic** — core service logic covered with JUnit + Mockito.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot |
| Security | Spring Security, JWT |
| Data Access | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Testing | JUnit 5, Mockito |
| Build Tool | Maven |
| API Testing | Postman |

---

## Architecture

```
Client (Postman / Mobile App)
        │
        ▼
   Controller  ──►  validates request, delegates to Service
        │
        ▼
    Service    ──►  business logic, locking strategy, validation
        │
        ▼
  Repository   ──►  Spring Data JPA / Hibernate
        │
        ▼
   PostgreSQL
```

---

## Core Design Decision: Handling Concurrent Stock Updates

**Problem:** two staff members simultaneously process a stock-out transaction for the same product. Without proper concurrency control, both requests could read the same "before" quantity and overwrite each other's update, resulting in incorrect stock levels.

**Solution:**
1. When a stock transaction is created, the product row is fetched with a **pessimistic lock** (`SELECT ... FOR UPDATE`), so a second concurrent request on the same product must wait until the first transaction completes.
2. For update paths that don't acquire this lock (e.g. an admin editing a product's name or price), the `@Version` column provides **optimistic locking** — if a conflicting update is detected, the operation is retried automatically up to a fixed number of attempts before failing with a clear `409 Conflict` response.

This two-layer approach protects the same data from two different kinds of concurrent write paths.

---
## Future Enhancements
- Approval workflow for stock transactions (pending → approved/rejected) before inventory is affected, allowing safer deletion of products with only unapproved transactions.
---

## API Overview

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/auth/login` | Authenticate and receive JWT | Public |
| GET | `/api/product/list` | List all products | Admin, Staff |
| POST | `/api/product` | Create a new product | Admin |
| PUT | `/api/product/update/{id}` | Update product details | Admin |
| POST | `/api/stock/transaction` | Create a stock in/out transaction | Admin, Staff |
| GET | `/api/category` | List categories | Admin, Staff |
| POST | `/api/category` | Create a category | Admin |

*(See controller classes in `src/main/java/.../controller` for the full list.)*

---

## Screenshots

> _Add screenshots here before sharing this repo — this section is what a recruiter or tech lead will look at first without running the project themselves._

**1. Successful login — JWT issued**
`[screenshot: Postman POST /api/auth/login response]`

**2. Creating a stock-out transaction**
`[screenshot: Postman POST /api/stock/transaction request + response]`

**3. Rejected stock-out — insufficient inventory (400)**
`[screenshot: Postman response showing the validation error]`

**4. Optimistic lock conflict (409)**
`[screenshot: Postman response for a version conflict]`

**5. Unit tests passing**
`[screenshot: IntelliJ test run panel, all green]`

---

## Running Locally

```bash
# 1. Clone the repository
git clone https://github.com/jeremyphamReal/warehouse-manager-systems.git

# 2. Configure your database connection in
#    src/main/resources/application.properties

# 3. Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## Running Tests

```bash
mvn test
```

---

## Git Workflow

This project follows a feature-branch workflow:

```
feature/xxx  →  develop  →  master
```

Each feature (category API, product API, security, inventory transactions) was developed on its own branch and merged into `develop` once complete.

---

## Author


**Pham Duc Binh**
[GitHub](https://github.com/jeremyphamReal) · [LinkedIn](https://www.linkedin.com/in/binh-pham-real-hi/)
