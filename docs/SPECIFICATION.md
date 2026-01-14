# SonarShowcase Application Specification

**Version:** 1.2.0  
**Last Updated:** January 2025  
**Purpose:** Complete specification of application behavior, requirements, and constraints for AI-assisted development

> ⚠️ **CRITICAL:** This document defines the **REQUIRED BEHAVIOR** of the application. Any code changes must maintain these behaviors unless explicitly documented as intentional changes.

---

## Table of Contents

1. [Application Overview](#application-overview)
2. [Architecture Requirements](#architecture-requirements)
3. [Data Models](#data-models)
4. [API Specifications](#api-specifications)
5. [Business Rules](#business-rules)
6. [Security Requirements](#security-requirements)
7. [Frontend Requirements](#frontend-requirements)
8. [Build & Deployment](#build--deployment)
9. [Testing Requirements](#testing-requirements)
10. [Intentional Issues](#intentional-issues)

---

## Application Overview

### Purpose
SonarShowcase is a **demonstration monolith application** designed to showcase SonarCloud's static analysis capabilities. It intentionally contains security vulnerabilities, bugs, and code smells for educational purposes.

### Key Characteristics
- **Monolith Architecture**: Spring Boot serves both REST API and React frontend
- **Technology Stack**: Java 21, Spring Boot 3.2.0, React/TypeScript, PostgreSQL
- **Single JAR Deployment**: Frontend is packaged as static resources in the backend JAR
- **Educational Purpose**: Contains intentional issues for SonarCloud analysis

### ⚠️ CRITICAL CONSTRAINTS
1. **DO NOT FIX** intentional security vulnerabilities (they are for demonstration)
2. **DO NOT FIX** intentional code smells (they are for SonarCloud analysis)
3. **MAINTAIN** all existing API endpoints and their behaviors
4. **PRESERVE** all business logic rules exactly as implemented
5. **KEEP** the monolith architecture (do not split into microservices)

---

## Architecture Requirements

### System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Single JAR Deployment                  │
│  ┌─────────────────────────────────────────────────────┐│
│  │              Spring Boot Application                ││
│  │  ┌─────────────────┐    ┌─────────────────────────┐││
│  │  │   REST API      │    │   Static Resources      │││
│  │  │   /api/v1/*     │    │   (React SPA)           │││
│  │  └─────────────────┘    └─────────────────────────┘││
│  └─────────────────────────────────────────────────────┘│
│                            │                             │
└────────────────────────────┼─────────────────────────────┘
                             ▼
                      ┌─────────────┐
                      │  PostgreSQL │
                      └─────────────┘
```

### Required Components

1. **Backend Module** (`backend/`)
   - Spring Boot application
   - REST API endpoints at `/api/v1/*`
   - Serves React frontend from `classpath:/static/`
   - SPA routing support via `SpaController`

2. **Frontend Module** (`frontend/`)
   - React/TypeScript application
   - Built with Vite
   - Output to `dist/` directory
   - Packaged into backend JAR as static resources

3. **Malicious Attic Module** (`malicious-attic/`) - **Test/Demo Module**
   - Test module for supply chain security scanning demonstration
   - Contains malicious npm packages (chai-tests-async, json-mappings, yunxohang10, jwtdapp)
   - Not built by Maven (packaging: `pom`)
   - Exists solely for SonarQube to detect supply chain vulnerabilities
   - **Not part of the main application** - for security scanning demo only

4. **Database**
   - PostgreSQL 15
   - Connection: `jdbc:postgresql://localhost:5432/sonarshowcase`
   - Auto-initialization via `DataInitializer`

### Routing Rules

**MUST BE PRESERVED:**
- API requests: `/api/v1/*` → REST controllers
- Static resources: `/static/*`, `/assets/*`, `/favicon.ico` → Static files
- All other requests: → Forward to `/index.html` (SPA routing)

---

## Data Models

### User Entity

**Table:** `users`  
**Required Fields:**
- `id` (Long, auto-generated)
- `username` (String, required)
- `email` (String, required)
- `password` (String, plain text - intentional security issue)
- `creditCardNumber` (String, optional)
- `ssn` (String, optional)
- `role` (String: "USER" | "ADMIN", defaults to "USER")
- `active` (Boolean, defaults to true)
- `createdAt` (Date)
- `updatedAt` (Date)

**Relationships:**
- One-to-Many with `Order` (cascade delete)

**Pre-seeded Data (MUST EXIST ON STARTUP):**
| Username | Email | Password | Role | Active |
|----------|-------|----------|------|--------|
| admin | admin@sonarshowcase.com | admin123 | ADMIN | true |
| john.doe | john@example.com | password123 | USER | true |
| jane.smith | jane@example.com | password123 | USER | true |
| bob.wilson | bob@example.com | password123 | USER | false |

### Order Entity

**Table:** `orders`  
**Required Fields:**
- `id` (Long, auto-generated)
- `orderNumber` (String, auto-generated format: `ORD-{timestamp}-{uuid8}`)
- `totalAmount` (BigDecimal, required)
- `status` (String: "PENDING" | "PROCESSING" | "SHIPPED" | "DELIVERED" | "CANCELLED")
- `orderDate` (Date, auto-set on creation)
- `shippingAddress` (String, optional)
- `notes` (String, optional)

**Relationships:**
- Many-to-One with `User`
- Many-to-Many with `Product` (via `order_products` join table)

**Auto-Generation Rules:**
- `orderNumber`: `"ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8)`
- `orderDate`: Current date/time on creation
- `status`: Defaults to "PENDING"

### Product Entity

**Table:** `products`  
**Required Fields:**
- `id` (Long, auto-generated)
- `name` (String, required)
- `description` (String, optional)
- `price` (BigDecimal, required)
- `quantity` (Integer, optional)
- `category` (String, optional)
- `sku` (String, optional)
- `available` (Boolean, optional)

### ActivityLog Entity

**Table:** `activity_logs`  
**Required Fields:**
- `id` (Long, auto-generated)
- `userId` (Long, required)
- `action` (String, required) - e.g., "LOGIN", "PROFILE_UPDATE", "ORDER_CREATE", "ADMIN_ACTION", "PASSWORD_CHANGE"
- `details` (String, optional) - Description of the activity
- `timestamp` (Date, required) - When the activity occurred
- `ipAddress` (String, optional) - IP address from which the activity originated

**Pre-seeded Data (MUST EXIST ON STARTUP):**
- Sample activity logs are created for existing users with various timestamps (current date, -1 day, -2 days, -5 days, -10 days) to enable date range filtering testing

---

## API Specifications

### Base URL
All API endpoints are prefixed with `/api/v1`

### Core Endpoints

#### Health & Info

**GET `/api/v1/health`**
- **Purpose:** System health check
- **Response:** `{"status": "UP", "timestamp": Date, "application": "SonarShowcase", "version": "1.2.0"}`
- **Status Codes:** 200 OK

**GET `/api/v1/info`**
- **Purpose:** System information (⚠️ intentional security issue - exposes sensitive data)
- **Response:** System properties (java.version, os.name, user.name, user.dir, database.url)
- **Status Codes:** 200 OK

#### Users API

**GET `/api/v1/users`**
- **Purpose:** Get all users
- **Response:** `List<User>` (⚠️ includes passwords - intentional security issue)
- **Status Codes:** 200 OK
- **Behavior:** Returns all users from database

**GET `/api/v1/users/{id}`**
- **Purpose:** Get user by ID
- **Path Parameters:** `id` (Long)
- **Response:** `User` (⚠️ includes password - intentional security issue)
- **Status Codes:** 200 OK, 500 if not found (NPE - intentional reliability issue)
- **Behavior:** Throws exception if user not found (intentional bug)

**GET `/api/v1/users/search?q={query}`**
- **Purpose:** Search users (in-memory search)
- **Query Parameters:** `q` (String)
- **Response:** `List<User>`
- **Status Codes:** 200 OK
- **Behavior:** Loads all users, filters in memory by username/email contains query

**POST `/api/v1/users`**
- **Purpose:** Create new user
- **Request Body:** `UserDto {username, email, password, role?}`
- **Response:** `User`
- **Status Codes:** 200 OK
- **Business Rules:**
  - If `role` is null, defaults to "USER"
  - Password stored in plain text (intentional security issue)
  - No validation (intentional maintainability issue)

**PUT `/api/v1/users/{id}/password`**
- **Purpose:** Update password (⚠️ insecure)
- **Path Parameters:** `id` (Long)
- **Query Parameters:** `oldPassword` (String), `newPassword` (String)
- **Response:** `"Password updated"` or `"Wrong password"`
- **Status Codes:** 200 OK, 400 Bad Request
- **Behavior:** 
  - Passwords in URL (intentional security issue)
  - Plain text comparison (intentional security issue)
  - No password strength validation (intentional security issue)

**POST `/api/v1/users/{id}/reset-token`**
- **Purpose:** Generate password reset token (🔴 security vulnerability)
- **Path Parameters:** `id` (Long)
- **Response:** JSON object with `userId`, `username`, `resetToken`, and `message`
- **Status Codes:** 200 OK, 404 Not Found (if user not found)
- **Behavior:**
  - Uses `java.util.Random` instead of `SecureRandom` (intentional security issue - java:S5445)
  - Generates a 32-character alphanumeric token
  - Returns token in response (should be sent via secure channel in production)
  - NPE risk: uses `.get()` on Optional without check (intentional reliability issue)

**DELETE `/api/v1/users/{id}`**
- **Purpose:** Delete user
- **Path Parameters:** `id` (Long)
- **Response:** `"Deleted"`
- **Status Codes:** 200 OK
- **Behavior:** No authorization check (intentional security issue)

#### Orders API

**GET `/api/v1/orders`**
- **Purpose:** Get all orders
- **Response:** `List<Order>`
- **Status Codes:** 200 OK

**GET `/api/v1/orders/{id}`**
- **Purpose:** Get order by ID
- **Path Parameters:** `id` (Long)
- **Response:** `Order`
- **Status Codes:** 200 OK, 500 if not found (NPE - intentional reliability issue)

**GET `/api/v1/orders/user/{userId}`**
- **Purpose:** Get orders by user ID
- **Path Parameters:** `userId` (Long)
- **Response:** `List<Order>`
- **Status Codes:** 200 OK

**POST `/api/v1/orders`**
- **Purpose:** Create new order
- **Request Body:** `Order {user: {id}, totalAmount, shippingAddress?, notes?}`
- **Response:** `Order`
- **Status Codes:** 200 OK
- **Business Rules:**
  - Auto-generates `orderNumber` using format: `ORD-{timestamp}-{uuid8}`
  - Sets `orderDate` to current date/time
  - Sets `status` to "PENDING"
  - Validates user exists (throws NPE if not - intentional bug)

**POST `/api/v1/orders/{id}/discount?code={code}`**
- **Purpose:** Apply discount code
- **Path Parameters:** `id` (Long)
- **Query Parameters:** `code` (String)
- **Response:** `Order` (with updated totalAmount)
- **Status Codes:** 200 OK
- **Business Rules:**
  - Valid codes: `SUMMER2023` (15%), `VIP` (25%), `EMPLOYEE` (50%)
  - Discount applied to `totalAmount`
  - **BUG:** Changes not persisted to database (intentional maintainability issue)

#### Activity Logs API

**GET `/api/v1/activity-logs`**
- **Purpose:** Get all activity logs
- **Response:** `List<ActivityLog>`
- **Status Codes:** 200 OK
- **Behavior:** Returns all activity logs from database

**GET `/api/v1/activity-logs/search?startDate={date}&endDate={date}&userId={id}`**
- **Purpose:** Search activity logs by date range (⚠️ SQL INJECTION VULNERABILITY)
- **Query Parameters:** 
  - `startDate` (String, required) - Start date for filtering
  - `endDate` (String, required) - End date for filtering
  - `userId` (String, optional) - User ID filter
- **Response:** `List<ActivityLog>`
- **Status Codes:** 200 OK, 500 if SQL error
- **Behavior:** 
  - User input directly concatenated into SQL query without sanitization (intentional security issue)
  - Clear source-to-sink path: HTTP params → Controller → Service → SQL execution
  - Attack examples:
    - `startDate=2025-01-01' OR '1'='1'--` (bypasses date filter)
    - `userId=1' UNION SELECT * FROM users--` (extracts user data)

**GET `/api/v1/activity-logs/user/{userId}`**
- **Purpose:** Get activity logs by user ID
- **Path Parameters:** `userId` (Long)
- **Response:** `List<ActivityLog>`
- **Status Codes:** 200 OK
- **Behavior:** Returns all activity logs for the specified user

**POST `/api/v1/activity-logs`**
- **Purpose:** Create new activity log entry
- **Request Body:** `ActivityLog {userId, action, details?, timestamp?, ipAddress?}`
- **Response:** `ActivityLog`
- **Status Codes:** 200 OK
- **Business Rules:**
  - If `timestamp` is null, auto-sets to current date/time
  - No validation (intentional maintainability issue)

### Vulnerable Endpoints (Security Demo)

These endpoints **MUST REMAIN VULNERABLE** for educational purposes:

#### SQL Injection Endpoints

**GET `/api/v1/users/login?username={username}&password={password}`**
- **Vulnerability:** SQL Injection (S3649)
- **Attack Examples:**
  - `username=admin'--&password=anything` (authentication bypass)
  - `username=' OR '1'='1'--&password=x` (login as first user)
- **Behavior:** Direct string concatenation in SQL query

**GET `/api/v1/users/vulnerable-search?term={term}`**
- **Vulnerability:** SQL Injection via UNION
- **Attack Example:** `term=' UNION SELECT * FROM users--`
- **Behavior:** User input directly in LIKE clause

**GET `/api/v1/users/sorted?orderBy={orderBy}`**
- **Vulnerability:** SQL Injection via ORDER BY
- **Attack Example:** `orderBy=username; DROP TABLE users;--`
- **Behavior:** User input directly in ORDER BY clause

**GET `/api/v1/activity-logs/search?startDate={date}&endDate={date}&userId={id}`**
- **Vulnerability:** SQL Injection (S3649) - Clear source-to-sink path
- **Attack Examples:**
  - `startDate=2025-01-01' OR '1'='1'--&endDate=2025-12-31` (bypasses date filter)
  - `userId=1' UNION SELECT id,username,email,password,role FROM users--` (extracts user data)
  - `startDate=2025-01-01'; DELETE FROM activity_logs;--` (deletes all logs)
- **Behavior:** 
  - User input directly concatenated into SQL query in service layer
  - Clear data flow: HTTP params → Controller → Service → EntityManager.executeQuery()
  - Demonstrates multi-layer SQL injection vulnerability

#### Path Traversal Endpoints

**GET `/api/v1/files/download?filename={filename}`**
- **Vulnerability:** Path Traversal (S2083)
- **Attack Example:** `filename=../../../etc/passwd`
- **Behavior:** No validation of filename, allows `../` sequences

**GET `/api/v1/files/read?path={path}`**
- **Vulnerability:** Path Traversal
- **Attack Example:** `path=/etc/passwd`
- **Behavior:** Direct use of user input in file path

**GET `/api/v1/files/profile?username={username}`**
- **Vulnerability:** Path Traversal
- **Attack Example:** `username=../../../etc/passwd`
- **Behavior:** Username used in file path without validation

**GET `/api/v1/files/logs?date={date}`**
- **Vulnerability:** Path Traversal
- **Attack Example:** `date=2025-01-01/../../../../etc/shadow`
- **Behavior:** Date parameter used in file path without validation

**GET `/api/v1/files/template?name={name}`**
- **Vulnerability:** Path Traversal (Template Inclusion)
- **Attack Example:** `name=../../../../etc/passwd`
- **Behavior:** Template name not sanitized

**POST `/api/v1/files/export?filename={filename}`**
- **Vulnerability:** Path Traversal (Write)
- **Attack Example:** `filename=../../../tmp/pwned.txt`
- **Behavior:** Allows writing to arbitrary file locations

**POST `/api/v1/files/extract?zipPath={path}&destDir={dir}`**
- **Vulnerability:** Zip Slip
- **Attack Example:** Malicious zip file with `../` in paths
- **Behavior:** Extracts files without path validation

**DELETE `/api/v1/files/delete?filename={filename}`**
- **Vulnerability:** Path Traversal (Delete)
- **Attack Example:** `filename=../../../important/data.db`
- **Behavior:** Allows deletion of arbitrary files

---

## Business Rules

### Order Pricing Calculation

**MUST BE IMPLEMENTED EXACTLY AS:**

```java
public BigDecimal calculateTotal(Order order) {
    BigDecimal subtotal = order.getTotalAmount();
    
    // Tax: 8.25%
    BigDecimal tax = subtotal.multiply(new BigDecimal("0.0825"));
    
    // Shipping: $5.99, FREE if subtotal > $50
    BigDecimal shipping = new BigDecimal("5.99");
    if (subtotal.compareTo(new BigDecimal("50")) > 0) {
        shipping = BigDecimal.ZERO;
    }
    
    // Discount: 10% if subtotal > $100
    if (subtotal.compareTo(new BigDecimal("100")) > 0) {
        subtotal = subtotal.multiply(new BigDecimal("0.9"));
    }
    
    return subtotal.add(tax).add(shipping);
}
```

**Rules:**
1. Tax: 8.25% of subtotal
2. Shipping: $5.99, FREE if subtotal > $50
3. Discount: 10% off if subtotal > $100 (applied before tax)

### Discount Codes

**MUST BE IMPLEMENTED EXACTLY AS:**

| Code | Discount | Implementation |
|------|----------|----------------|
| `SUMMER2023` | 15% | `totalAmount * 0.15` |
| `VIP` | 25% | `totalAmount * 0.25` |
| `EMPLOYEE` | 50% | `totalAmount / 2` |

**Note:** Discount codes are applied via `POST /api/v1/orders/{id}/discount?code={code}` but changes are NOT persisted (intentional bug).

### Order Number Generation

**MUST BE IMPLEMENTED EXACTLY AS:**

```java
private String generateOrderNumber() {
    return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
}
```

**Format:** `ORD-{timestamp}-{uuid8}`  
**Example:** `ORD-1703001234567-a1b2c3d4`

### User Creation Defaults

**MUST BE IMPLEMENTED EXACTLY AS:**
- If `role` is null or empty, default to `"USER"`
- `active` defaults to `true` (if not specified)
- `createdAt` set to current date/time
- Password stored in plain text (intentional security issue)

---

## Security Requirements

### ⚠️ CRITICAL: Intentional Vulnerabilities

**DO NOT FIX** the following intentional security issues:

1. **SQL Injection** (3 endpoints)
2. **Path Traversal** (8 endpoints)
3. **Plain Text Passwords** (all password storage)
4. **No Authentication** (all endpoints publicly accessible)
5. **Sensitive Data Exposure** (passwords, SSN, credit cards in API responses)
6. **Hardcoded Credentials** (database, mail, payment services)
7. **XSS** (dangerouslySetInnerHTML in frontend)
8. **JWT in localStorage** (frontend)
9. **CORS Wildcard** (WebConfig)
10. **Weak Random Number Generator** (java:S5445) - Password reset token generation uses `java.util.Random` instead of `SecureRandom`

These are **REQUIRED** for SonarCloud demonstration purposes.

---

## Frontend Requirements

### Technology Stack
- React 18.2.0
- TypeScript 5.2.2
- Vite 5.0.0
- React Router 6.20.0

### Build Configuration
- **Output Directory:** `dist/`
- **Dev Server Port:** 3000
- **API Proxy:** `/api` → `http://localhost:8080`
- **Source Maps:** Enabled for production

### SPA Routing
- All non-API requests forward to `index.html`
- Client-side routing handled by React Router
- Server-side forwarding handled by `SpaController`

### Required Components
- `App.tsx` - Main application component
- `Dashboard.tsx` - User dashboard
- `UserList.tsx` - User list display
- `CommentDisplay.tsx` - Comment display (⚠️ XSS vulnerability)
- `FormComponent.tsx` - Form handling
- `Calculator.tsx` - Calculator component
- `DataTable.tsx` - Data table display

### Intentional Frontend Issues
- Excessive use of `any` type
- Incomplete useEffect dependency arrays
- XSS via `dangerouslySetInnerHTML`
- JWT stored in localStorage
- Console.log spam
- Prop drilling

**DO NOT FIX** these issues - they are for SonarCloud analysis.

---

## Build & Deployment

### Build Process

**MUST WORK AS:**

1. **Frontend Build:**
   ```bash
   cd frontend
   npm install
   npm run build
   ```
   - Outputs to `frontend/dist/`
   - Includes source maps

2. **Backend Build:**
   ```bash
   mvn clean package
   ```
   - Builds frontend via `frontend-maven-plugin`
   - Packages frontend `dist/` into backend JAR as `static/`
   - Output: `backend/target/sonarshowcase-backend-1.2.0-SNAPSHOT.jar`

3. **Full Build:**
   ```bash
   mvn clean install
   ```
   - Builds both modules
   - Runs tests
   - Generates coverage reports

### Docker Deployment

**MUST WORK AS:**

```bash
docker-compose up -d
```

- PostgreSQL on port 5432
- Application on port 8080
- Health check: `http://localhost:8080/api/v1/health`

### File Locations

**MUST BE PRESERVED:**
- Frontend static resources: `classpath:/static/`
- Frontend source: `frontend/src/`
- Backend source: `backend/src/main/java/`
- Tests: `backend/src/test/java/`, `frontend/test/`
- Coverage: `backend/target/site/jacoco/`, `frontend/coverage/`

---

## Testing Requirements

### Test Coverage

**MUST EXIST:**
- Backend unit tests in `backend/src/test/java/`
- Frontend tests in `frontend/test/`
- JaCoCo coverage reports for backend
- LCOV coverage reports for frontend

### Test Execution

**MUST WORK:**
```bash
# Backend tests
mvn test

# Frontend tests
cd frontend && npm test

# Coverage
mvn verify  # Backend
cd frontend && npm run test:coverage  # Frontend
```

### Intentional Test Issues

**DO NOT FIX:**
- Skeleton tests with no assertions
- Tests with missing coverage
- Incomplete test suites

These are for SonarCloud analysis.

---

## Intentional Issues

### Categories of Intentional Issues

1. **Security (15+ issues)**
   - SQL Injection
   - Path Traversal
   - Hardcoded credentials
   - XSS
   - Weak cryptography

2. **Reliability (10+ issues)**
   - Null pointer risks
   - Resource leaks
   - Swallowed exceptions
   - Race conditions

3. **Maintainability (50+ issues)**
   - God class (DataManager.java)
   - Magic numbers
   - 'any' type abuse
   - Duplicated code
   - Poor naming

**⚠️ CRITICAL:** These issues are **REQUIRED** for the application's educational purpose. Do not fix them unless explicitly requested.

---

## Change Control

### When Modifying Code

**BEFORE making any changes:**
1. Review this specification
2. Identify which behaviors must be preserved
3. Verify changes don't break existing functionality
4. Ensure intentional issues remain (unless explicitly fixing)

### Breaking Changes

**MUST NOT:**
- Remove or change API endpoint paths
- Change business logic rules (pricing, discounts, order numbers)
- Fix intentional security vulnerabilities
- Change data model structure
- Remove pre-seeded users
- Change build/deployment process

**MAY:**
- Add new endpoints (document them)
- Add new features (document them)
- Fix bugs that are NOT intentional
- Improve documentation
- Add tests (but keep intentional test issues)

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | January 2025 | Initial specification |
| 1.2.0 | January 2025 | Version consistency updates, use version variables |

---

**END OF SPECIFICATION**

*This document is the authoritative source for application behavior. Code must match this specification.*

