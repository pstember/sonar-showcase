# SonarShowcase API Reference Card

**Quick Reference** - All API endpoints at a glance

---

## Base URL
```
http://localhost:8080/api/v1
```

---

## Core Endpoints

### Health & Info

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | System health check |
| GET | `/info` | System information ⚠️ exposes sensitive data |

### Users

| Method | Endpoint | Description | Security |
|--------|----------|-------------|----------|
| GET | `/users` | Get all users | ⚠️ Returns passwords |
| GET | `/users/{id}` | Get user by ID | ⚠️ Returns password |
| GET | `/users/search?q={query}` | Search users | Safe (in-memory) |
| POST | `/users` | Create user | ⚠️ No validation |
| PUT | `/users/{id}/password?old={old}&new={new}` | Update password | ⚠️ Insecure |
| POST | `/users/{id}/reset-token` | Generate password reset token | 🔴 Weak random generator (java:S5445) |
| DELETE | `/users/{id}` | Delete user | ⚠️ No authorization |

### Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/orders` | Get all orders |
| GET | `/orders/{id}` | Get order by ID |
| GET | `/orders/user/{userId}` | Get orders by user |
| POST | `/orders` | Create order |
| POST | `/orders/{id}/discount?code={code}` | Apply discount code |

**Discount Codes:**
- `SUMMER2023` → 15% discount
- `VIP` → 25% discount
- `EMPLOYEE` → 50% discount

### Activity Logs

| Method | Endpoint | Description | Security |
|--------|----------|-------------|----------|
| GET | `/activity-logs` | Get all activity logs | Safe |
| GET | `/activity-logs/search?startDate={d}&endDate={d}&userId={id}` | Search by date range | 🔴 SQL Injection |
| GET | `/activity-logs/user/{userId}` | Get logs by user ID | Safe |
| POST | `/activity-logs` | Create activity log | ⚠️ No validation |

---

## Vulnerable Endpoints (Security Demo)

### SQL Injection

| Method | Endpoint | Vulnerability | Attack Example |
|--------|----------|---------------|----------------|
| GET | `/users/login?username={u}&password={p}` | 🔴 SQL Injection | `username=admin'--` |
| GET | `/users/vulnerable-search?term={term}` | 🔴 SQL Injection | `term=' UNION SELECT * FROM users--` |
| GET | `/users/sorted?orderBy={col}` | 🔴 SQL Injection | `orderBy=username; DROP TABLE users;--` |
| GET | `/activity-logs/search?startDate={d}&endDate={d}&userId={id}` | 🔴 SQL Injection | `startDate=2025-01-01' OR '1'='1'--` |

### Path Traversal

| Method | Endpoint | Vulnerability | Attack Example |
|--------|----------|---------------|----------------|
| GET | `/files/download?filename={name}` | 🔴 Path Traversal | `filename=../../../etc/passwd` |
| GET | `/files/read?path={path}` | 🔴 Path Traversal | `path=/etc/passwd` |
| GET | `/files/profile?username={u}` | 🔴 Path Traversal | `username=../../../etc/passwd` |
| GET | `/files/logs?date={date}` | 🔴 Path Traversal | `date=2025-01-01/../../../../etc/shadow` |
| GET | `/files/template?name={name}` | 🔴 Path Traversal | `name=../../../../etc/passwd` |
| POST | `/files/export?filename={name}` | 🔴 Path Traversal (Write) | `filename=../../../tmp/pwned.txt` |
| POST | `/files/extract?zipPath={p}&destDir={d}` | 🔴 Zip Slip | Malicious zip with `../` |
| DELETE | `/files/delete?filename={name}` | 🔴 Path Traversal (Delete) | `filename=../../../important/data.db` |

---

## Business Rules

### Order Pricing
- **Tax:** 8.25% of subtotal
- **Shipping:** $5.99, **FREE** if subtotal > $50
- **Discount:** 10% off if subtotal > $100

### Order Number Format
```
ORD-{timestamp}-{uuid8}
Example: ORD-1703001234567-a1b2c3d4
```

---

## Request Examples

### Create User
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Create Order
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "user": {"id": 1},
    "totalAmount": 75.00,
    "shippingAddress": "123 Main St"
  }'
```

### Apply Discount
```bash
curl -X POST "http://localhost:8080/api/v1/orders/1/discount?code=SUMMER2023"
```

### Search Activity Logs (Vulnerable)
```bash
# Normal usage
curl "http://localhost:8080/api/v1/activity-logs/search?startDate=2025-01-01&endDate=2025-12-31"

# SQL Injection attack (bypass date filter)
curl "http://localhost:8080/api/v1/activity-logs/search?startDate=2025-01-01' OR '1'='1'--&endDate=2025-12-31"
```

---

## Interactive Documentation

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **OpenAPI YAML:** http://localhost:8080/v3/api-docs.yaml

---

## Legend

- ⚠️ = Security issue or intentional vulnerability
- 🔴 = Critical security vulnerability
- Safe = No known security issues (but may have other bugs)

---

**Note:** This is a demonstration application with intentional security vulnerabilities for educational purposes. DO NOT use in production!

---

*For complete specification, see `docs/SPECIFICATION.md`*

