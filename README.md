# SonarShowcase

A demonstration monolith application (Spring Boot + React/TypeScript) designed to showcase SonarCloud's static analysis capabilities.

> ⚠️ **WARNING:** This application intentionally contains security vulnerabilities, bugs, and code smells for educational purposes. DO NOT use in production!

## Architecture

This is a **monolith architecture** where:
- **Backend**: Spring Boot (Java 21) serves both API endpoints and the React frontend
- **Frontend**: React/TypeScript built with Vite, packaged as static resources in the JAR
- **Database**: PostgreSQL
- **SPA Routing**: All non-API requests are forwarded to `index.html` to enable client-side routing (handled by `SpaController`)

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

## Project Structure

```
sonar-demo/
├── pom.xml                    # Parent Maven POM
├── Dockerfile                 # Multi-stage build for monolith
├── docker-compose.yml         # PostgreSQL + App
├── backend/
│   ├── pom.xml                # Backend module (depends on frontend)
│   └── src/
│       ├── main/java/         # Java source code
│       └── test/java/         # Java tests
├── frontend/
│   ├── pom.xml                # Frontend module (builds React app)
│   ├── package.json
│   ├── vite.config.ts
│   └── src/                   # React/TypeScript source
└── malicious-attic/
    ├── pom.xml                # Test module for supply chain security scanning
    ├── package.json            # Contains malicious npm packages for demo
    └── package-lock.json      # Lockfile with malicious dependencies
```

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 21+ (for local development)
- Node.js 24+ (for local development)
- Maven 3.9+ (for local development)

### Running with Docker (Recommended)

```bash
# Start the full stack (PostgreSQL + Monolith App)
docker-compose up -d

# Wait for services to start (takes 1-2 minutes)
# Application: http://localhost:8080
# API Health:  http://localhost:8080/api/v1/health
```

### Running Locally

1. **Start PostgreSQL:**
```bash
docker-compose up -d postgres
```

2. **Build and run the monolith:**
```bash
# From the project root
mvn clean install
cd backend
mvn spring-boot:run
```

3. **For frontend development with hot reload:**
```bash
# In a separate terminal
cd frontend
npm install
npm run dev
# Frontend dev server: http://localhost:3000 (proxies API to :8080)
```

## Building the Application

### Full Build (Backend + Frontend + Documentation)

Running `mvn clean install` will automatically:
- Build the frontend React application
- Generate TypeDoc documentation for frontend
- Build the backend Spring Boot application  
- Generate JavaDoc documentation for backend
- Package everything into JARs

### Full Build (Backend + Frontend)

```bash
# Build everything from the root
mvn clean package

# The executable JAR is at: backend/target/sonarshowcase-backend-1.2.0-SNAPSHOT.jar
```

### Skip Frontend Build

```bash
# Build only backend (requires frontend to be pre-built)
mvn clean package -Dfrontend-maven-plugin.skip=true
```

## Running SonarCloud Analysis

Analysis is performed against [SonarCloud](https://sonarcloud.io). No local SonarQube instance is needed.

### Prerequisites

1. Create a project on [SonarCloud](https://sonarcloud.io)
2. Generate a token: Account → Security → Generate Tokens
3. Set the token as an environment variable:
```bash
export SONAR_TOKEN=your_token_here
```

### Analyze with Maven

```bash
# Build with tests and coverage, then analyze
mvn clean verify sonar:sonar

# Or analyze without rebuilding
mvn sonar:sonar
```

### Analyze with sonar-scanner (Alternative)

```bash
# Install sonar-scanner if not already installed
# npm install -g sonar-scanner

# Run from project root
sonar-scanner -Dsonar.token=$SONAR_TOKEN
```

### View Results

Open your project on [SonarCloud](https://sonarcloud.io) to explore the analysis results.

## Documentation

### Automatic Documentation Generation

Documentation is **automatically generated** during the Maven build:

- **JavaDoc (Backend):** Generated during build → `backend/target/site/apidocs/`
- **TypeDoc (Frontend):** Generated during build → `frontend/target/site/typedoc/`

Simply run:
```bash
mvn clean install
```

Both documentation sets will be created automatically.

**View documentation:**
```bash
# Backend JavaDoc
open backend/target/site/apidocs/index.html

# Frontend TypeDoc
open frontend/target/site/typedoc/index.html
```

See `docs/AUTOMATED_DOCUMENTATION.md` for details and `docs/DOCUMENTATION_LOCATIONS.md` for file locations.

### Interactive API Documentation (Swagger UI)

The application includes automatically generated API documentation using SpringDoc OpenAPI:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **OpenAPI YAML:** http://localhost:8080/v3/api-docs.yaml

The documentation is automatically generated from the code and includes:
- All REST API endpoints (27 endpoints across 4 controllers)
- Request/response schemas
- Parameter descriptions with examples
- Security warnings for vulnerable endpoints
- Response codes and descriptions

> **Note:** The documentation is generated at runtime. Start the application to access it.
> 
> **Testing Guide:** See `docs/SWAGGER_TESTING.md` for detailed instructions on testing Swagger UI and verifying all endpoints.

### Specification Documents

For detailed specifications and requirements:
- **Application Specification:** `docs/SPECIFICATION.md` - Complete specification of application behavior
- **API Reference Card:** `docs/API_REFERENCE_CARD.md` - Quick reference for all endpoints
- **Swagger Testing Guide:** `docs/SWAGGER_TESTING.md` - Guide for testing Swagger UI and generating documentation
- **Automated Documentation:** `docs/AUTOMATED_DOCUMENTATION.md` - How documentation is generated during build
- **JavaDoc Guide:** `docs/JAVADOC_GENERATION.md` - Backend JavaDoc generation guide
- **TypeDoc Guide:** `docs/TYPEDOC_GENERATION.md` - Frontend TypeDoc generation guide
- **Business Logic:** `docs/business-logic.md` - Business flows and rules
- **API Reference:** `docs/api-spec.md` - API endpoint reference (legacy, see Swagger UI for current)
- **AI Assistant Guide:** `docs/AI_ASSISTANT_GUIDE.md` - Guide for AI assistants working on this codebase

### Scanner Configuration

This project uses a **hybrid configuration approach** for SonarQube scanning:

1. **Maven Auto-Detection** (`sonar.maven.scanAll=True` in parent `pom.xml`):
   - SonarQube automatically detects Maven modules (backend, frontend, and malicious-attic)
   - Module-specific properties are defined in each module's `pom.xml` under `<properties>`

2. **Module-Specific Properties**:
   - **Backend** (`backend/pom.xml`): Uses standard Maven Java structure
     - Sources: `src/main/java`
     - Tests: `src/test/java`
     - Coverage: JaCoCo XML report at `target/site/jacoco/jacoco.xml`
   
   - **Frontend** (`frontend/pom.xml`): Custom TypeScript/JavaScript configuration
     - Sources: `src` (TypeScript/JavaScript files)
     - Tests: `test` directory
     - Test inclusions: `**/*.test.ts`, `**/*.test.tsx`
     - Exclusions: `**/node_modules/**`, `**/dist/**`, `**/build/**`, config files
     - Coverage: LCOV report at `coverage/lcov.info`
   
   - **Malicious Attic** (`malicious-attic/pom.xml`): Test module for supply chain security scanning
     - Packaging: `pom` (not built by Maven, exists only for SonarQube scanning)
     - Sources: `.` (package.json and package-lock.json)
     - Contains malicious npm packages for supply chain vulnerability detection
     - Exclusions: `**/node_modules/**`, `**/dist/**`, `**/build/**`, config files

3. **Global Configuration** (Optional):
   - If not using Maven, you can create a `sonar-project.properties` file with:
     - Project identification (key, name, organization)
     - Module definitions
   - Note: Maven auto-detection is the primary method and takes precedence (all configuration is in `pom.xml`)

**Key Point**: The frontend module's source paths are explicitly configured in `frontend/pom.xml` because it doesn't follow Maven's standard Java directory structure. Without these properties, SonarQube would only index `pom.xml` instead of the TypeScript/JavaScript source files.

## API Endpoints

### Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/health` | Health check |
| GET | `/api/v1/info` | System information (⚠️ exposes sensitive data) |
| GET | `/api/v1/users` | Get all users |
| GET | `/api/v1/users/{id}` | Get user by ID |
| GET | `/api/v1/users/search?q={query}` | Search users (in-memory search) |
| POST | `/api/v1/users` | Create user |
| PUT | `/api/v1/users/{id}/password` | Update password (⚠️ insecure) |
| POST | `/api/v1/users/{id}/reset-token` | Generate password reset token (🔴 weak random generator) |
| DELETE | `/api/v1/users/{id}` | Delete user |
| GET | `/api/v1/orders` | Get all orders |
| GET | `/api/v1/orders/{id}` | Get order by ID |
| GET | `/api/v1/orders/user/{userId}` | Get orders by user ID |
| POST | `/api/v1/orders` | Create order |
| POST | `/api/v1/orders/{id}/discount?code={code}` | Apply discount code |
| GET | `/api/v1/activity-logs` | Get all activity logs |
| GET | `/api/v1/activity-logs/user/{userId}` | Get activity logs by user ID |
| POST | `/api/v1/activity-logs` | Create activity log |

### Vulnerable Endpoints (Security Demo)

| Method | Endpoint | Vulnerability |
|--------|----------|---------------|
| GET | `/api/v1/users/login?username=X&password=Y` | 🔴 SQL Injection |
| GET | `/api/v1/users/vulnerable-search?term=X` | 🔴 SQL Injection |
| GET | `/api/v1/users/sorted?orderBy=X` | 🔴 SQL Injection |
| GET | `/api/v1/activity-logs/search?startDate=X&endDate=Y&userId=Z` | 🔴 SQL Injection |
| GET | `/api/v1/files/download?filename=X` | 🔴 Path Traversal |
| GET | `/api/v1/files/read?path=X` | 🔴 Path Traversal |
| GET | `/api/v1/files/profile?username=X` | 🔴 Path Traversal |
| GET | `/api/v1/files/logs?date=X` | 🔴 Path Traversal |
| GET | `/api/v1/files/template?name=X` | 🔴 Path Traversal (Template Inclusion) |
| POST | `/api/v1/files/export?filename=X` | 🔴 Path Traversal (Write) |
| POST | `/api/v1/files/extract?zipPath=X&destDir=Y` | 🔴 Zip Slip Vulnerability |
| DELETE | `/api/v1/files/delete?filename=X` | 🔴 Path Traversal (Delete) |

### Example Requests

```bash
# Health check
curl http://localhost:8080/api/v1/health

# Get all users
curl http://localhost:8080/api/v1/users

# Create a user
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "email": "john@example.com", "password": "password123"}'
```

## Intentional Issues for SonarCloud Demo

### Security (15+ issues)

#### SQL Injection (S3649)
| Endpoint | Attack Vector | Description |
|----------|---------------|-------------|
| `GET /api/v1/users/login` | `username=admin'--` | Authentication bypass via SQL comment |
| `GET /api/v1/users/vulnerable-search` | `term=' UNION SELECT...` | Data extraction via UNION injection |
| `GET /api/v1/users/sorted` | `orderBy=username; DROP TABLE` | ORDER BY clause injection |
| `GET /api/v1/activity-logs/search` | `startDate=2025-01-01' OR '1'='1'--` | Date range bypass via SQL injection |
| `GET /api/v1/activity-logs/search` | `userId=1' UNION SELECT * FROM users--` | Data extraction via UNION injection |
| `UserRepositoryCustomImpl` | Internal methods | SQL concat in findUsersBySearch, authenticateUser |
| `ActivityLogService` | `getActivityLogsByDateRange()` | Clear source-to-sink path: HTTP params → Service → SQL |

#### Path Traversal (S2083)
| Endpoint | Attack Vector | Description |
|----------|---------------|-------------|
| `GET /api/v1/files/download` | `filename=../../../etc/passwd` | Read arbitrary files |
| `GET /api/v1/files/read` | `path=/etc/passwd` | Direct file read |
| `GET /api/v1/files/profile` | `username=../../../etc/passwd` | Profile path manipulation |
| `GET /api/v1/files/logs` | `date=2025/../../../etc/shadow` | Log date injection |
| `POST /api/v1/files/export` | `filename=../../../tmp/pwned` | Write arbitrary files |
| `DELETE /api/v1/files/delete` | `filename=../../../important` | Delete arbitrary files |

#### Supply Chain Security (SCA)
| Module | Malicious Package | Vulnerability ID | Description |
|--------|------------------|------------------|-------------|
| `malicious-attic` | `chai-tests-async` | MAL-2026-172 | Embedded malicious code (CWE-506) |
| `malicious-attic` | `json-mappings` | MAL-2026-160 | Embedded malicious code (CWE-506) |
| `malicious-attic` | `yunxohang10` | MAL-2026-182 | Embedded malicious code (CWE-506) |
| `malicious-attic` | `jwtdapp` | MAL-2026-175 | Embedded malicious code (CWE-506) |

The `malicious-attic` module contains intentionally malicious npm packages for demonstrating SonarQube's supply chain security analysis capabilities. These packages are flagged in the OSV database and should trigger security alerts during scanning.

#### Other Security Issues
- Hardcoded credentials throughout (PaymentService, DatabaseConfig)
- XSS via dangerouslySetInnerHTML (CommentDisplay.tsx)
- Weak cryptography using MD5 (PasswordUtil)
- Weak random number generator (java:S5445) - Password reset token uses `java.util.Random` instead of `SecureRandom` (UserController)
- CORS wildcard configuration (WebConfig)
- JWT stored in localStorage (api.ts)

### Reliability (10+ issues)
- Null pointer risks
- Resource leaks
- Swallowed exceptions
- Race conditions
- Stale closure in useEffect

### Maintainability (50+ issues)
- God class (DataManager.java - 400+ lines)
- Magic numbers everywhere
- 'any' type abuse in TypeScript
- Duplicated validation code
- Poor naming conventions
- Console.log spam
- TODO/FIXME comments
- Skeleton tests with no assertions

## Development

### Backend Development

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend Development

```bash
cd frontend
npm install
npm run dev          # Start dev server with hot reload
npm run test         # Run tests
npm run test:coverage # Run tests with coverage
npm run build        # Production build
```

### Running Tests

```bash
# Run all tests
mvn test

# Run only backend tests
mvn test -pl backend

# Run only frontend tests
cd frontend && npm test
```

## License

MIT License

---

*This application is intentionally flawed for educational purposes.*
