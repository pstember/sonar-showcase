# Notification & Webhook Integration System

## Overview

The **Notification & Webhook Integration System** is a feature of the SonarShowcase e-commerce platform that enables sending notifications (email, SMS, push) and integrating with external systems via webhooks when order events occur.

This feature:
1. **Provides business value** - Essential for e-commerce order notifications and integrations
2. **Demonstrates SonarQube capabilities** - Contains 1-2 issues from every major SonarQube category
3. **Includes vulnerable dependencies** - Uses intentionally vulnerable third-party libraries for demonstration purposes

---

## Feature Description

### Business Value
- Send order confirmation notifications (email, SMS, push)
- Integrate with external systems via webhooks (inventory management, analytics, CRM)
- Allow customers to configure notification preferences
- Track notification delivery status
- Support webhook retries and error handling

### Technical Components

1. **NotificationService** - Core service for sending notifications
2. **WebhookService** - Handles webhook delivery to external URLs
3. **NotificationController** - REST API for managing notifications
4. **NotificationPreferences** - Entity for user notification settings
5. **WebhookConfiguration** - Entity for webhook endpoints
6. **NotificationHistory** - Entity for tracking sent notifications
7. **Frontend Components** - React components for notification preferences UI

---

## SonarQube Issues by Category

### 🔴 Security Vulnerabilities (S-prefixed rules)

#### 1. SQL Injection (S3649)
**Location**: `NotificationService.getNotificationHistory()`
```java
// VULNERABLE: Direct SQL concatenation
String query = "SELECT * FROM notification_history WHERE user_id = " + userId;
if (status != null) {
    query += " AND status = '" + status + "'";
}
```
**Attack Vector**: `userId=1 OR 1=1--` or `status='pending' OR '1'='1'--`

#### 2. Server-Side Request Forgery (S5131)
**Location**: `WebhookService.sendWebhook()`
```java
// VULNERABLE: No validation of webhook URL
URL webhookUrl = new URL(webhookConfig.getUrl());
HttpURLConnection conn = (HttpURLConnection) webhookUrl.openConnection();
```
**Attack Vector**: `http://localhost:8080/admin` or `file:///etc/passwd`

#### 3. Command Injection (S2083)
**Location**: `NotificationService.sendSMS()`
```java
// VULNERABLE: Command injection via phone number
String command = "sms-sender --to " + phoneNumber + " --message " + message;
Runtime.getRuntime().exec(command);
```
**Attack Vector**: `phoneNumber="1234567890; rm -rf /"`

#### 4. Insecure Deserialization (S5145)
**Location**: `WebhookService.processWebhookPayload()`
```java
// VULNERABLE: Deserializing untrusted data
ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(payload));
Object obj = ois.readObject();
```
**Attack Vector**: Malicious serialized object in webhook payload

#### 5. Sensitive Data Exposure (S4787)
**Location**: `NotificationService.sendEmail()`
```java
// VULNERABLE: Logging sensitive data
System.out.println("Sending email to: " + email + " with content: " + emailContent);
logger.info("Email sent with API key: " + EMAIL_API_KEY);
```
**Attack Vector**: Sensitive data in logs

#### 6. Hardcoded Credentials (S6418)
**Location**: `NotificationService` class
```java
// VULNERABLE: Hardcoded API keys
private static final String TWILIO_API_KEY = "AC1234567890abcdef";
private static final String SENDGRID_API_KEY = "SG.abcdef1234567890";
private static final String FIREBASE_SERVER_KEY = "AAAA1234567890:APA91b...";
```

#### 7. Weak Cryptography (S4790)
**Location**: `WebhookService.signWebhook()`
```java
// VULNERABLE: Using MD5 for webhook signature
MessageDigest md = MessageDigest.getInstance("MD5");
byte[] hash = md.digest(payload.getBytes());
String signature = new String(hash);
```

#### 8. XSS in Notification Content (S5131)
**Location**: `NotificationController.createNotification()`
```java
// VULNERABLE: No sanitization of user input
Notification notification = new Notification();
notification.setContent(userProvidedContent); // Could contain <script> tags
```

---

### 🐛 Bugs / Reliability Issues

#### 1. Null Pointer Exception (S2259)
**Location**: `NotificationService.sendNotification()`
```java
// BUG: NPE risk - user might be null
String email = user.getEmail(); // No null check
notificationService.sendEmail(email, content);
```

#### 2. Resource Leak (S2095)
**Location**: `WebhookService.sendWebhook()`
```java
// BUG: Connection not closed in all code paths
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("POST");
conn.getOutputStream().write(payload.getBytes());
// Missing: conn.disconnect() in finally block
```

#### 3. Array Index Out of Bounds (S2677)
**Location**: `NotificationService.parsePhoneNumber()`
```java
// BUG: No bounds checking
String[] parts = phoneNumber.split("-");
String countryCode = parts[0];
String number = parts[1]; // Could throw ArrayIndexOutOfBoundsException
```

#### 4. Swallowed Exception (S1181)
**Location**: `WebhookService.retryWebhook()`
```java
// BUG: Exception swallowed
try {
    sendWebhook(webhook);
} catch (Exception e) {
    // Swallowed - no logging, no handling
}
```

#### 5. Race Condition (S2886)
**Location**: `NotificationService.incrementRetryCount()`
```java
// BUG: Not thread-safe
private int retryCount = 0;
public void incrementRetryCount() {
    retryCount++; // Race condition in multi-threaded environment
}
```

#### 6. Stale Closure (Frontend)
**Location**: `NotificationPreferences.tsx`
```typescript
// BUG: Stale closure in useEffect
useEffect(() => {
    const timer = setTimeout(() => {
        savePreferences(preferences); // Uses stale 'preferences' value
    }, 1000);
    return () => clearTimeout(timer);
}, []); // Missing 'preferences' in dependency array
```

---

### 🟡 Code Smells / Maintainability Issues

#### 1. Duplicated Code (S1192)
**Location**: Multiple methods in `NotificationService`
```java
// DUPLICATION: Similar email sending logic in 3 different methods
public void sendOrderConfirmation(User user, Order order) {
    String email = user.getEmail();
    String subject = "Order Confirmation";
    String body = "Your order " + order.getOrderNumber() + " has been confirmed.";
    sendEmail(email, subject, body);
}

public void sendOrderShipped(User user, Order order) {
    String email = user.getEmail();
    String subject = "Order Shipped";
    String body = "Your order " + order.getOrderNumber() + " has been shipped.";
    sendEmail(email, subject, body);
}
// Similar pattern repeated 5+ times
```

#### 2. Complex Method (S3776)
**Location**: `NotificationService.processNotificationQueue()`
```java
// COMPLEXITY: Method with cyclomatic complexity > 15
public void processNotificationQueue() {
    List<Notification> queue = getQueue();
    for (Notification n : queue) {
        if (n.getType().equals("email")) {
            if (n.getPriority() > 5) {
                if (isBusinessHours()) {
                    // ... 10+ nested if statements
                }
            }
        }
        // ... 50+ lines of nested logic
    }
}
```

#### 3. Magic Numbers (S109)
**Location**: Throughout `NotificationService`
```java
// MAGIC NUMBERS: Hardcoded values without constants
if (retryCount > 3) { // What does 3 mean?
    markAsFailed();
}
Thread.sleep(5000); // What does 5000 mean?
if (priority > 10) { // What does 10 mean?
    sendImmediately();
}
```

#### 4. Long Parameter List (S107)
**Location**: `NotificationService.sendNotification()`
```java
// LONG PARAMETERS: 8+ parameters
public void sendNotification(
    String type, String recipient, String subject, String content,
    String priority, String channel, Map<String, String> metadata,
    boolean retryOnFailure) {
    // ...
}
```

#### 5. Dead Code (S1481)
**Location**: `NotificationService` class
```java
// DEAD CODE: Unused method
private void oldNotificationMethod() {
    // This method is never called
    System.out.println("Old implementation");
}

// DEAD CODE: Unreachable code
public void sendNotification() {
    return;
    System.out.println("This never executes");
}
```

#### 6. God Class (S138)
**Location**: `NotificationService` class
```java
// GOD CLASS: 500+ lines, too many responsibilities
public class NotificationService {
    // Handles email, SMS, push, webhooks, preferences, history, retries, etc.
    // Should be split into: EmailService, SMSService, PushService, WebhookService
}
```

#### 7. 'any' Type Abuse (TypeScript)
**Location**: `NotificationService.ts`
```typescript
// CODE SMELL: Excessive use of 'any' type
function processNotification(data: any): any {
    const result: any = {};
    result.status = data.status;
    return result;
}
```

#### 8. Console.log Statements (S106)
**Location**: Throughout notification code
```java
// CODE SMELL: Debug statements in production code
System.out.println("Processing notification: " + notificationId);
System.out.println("Sending to: " + recipient);
System.out.println("Status: " + status);
```

#### 9. TODO/FIXME Comments (S1135)
**Location**: Multiple files
```java
// TODO: Implement proper error handling
// FIXME: This is a temporary workaround
// HACK: Quick fix for production issue
```

---

### 📦 Third-Party Dependency Vulnerabilities

#### 1. Apache HttpClient (CVE-2020-13956)
**Dependency**: `org.apache.httpcomponents:httpclient:4.5.6`
```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.6</version> <!-- VULNERABLE: Should be 4.5.13+ -->
</dependency>
```
**Vulnerability**: HTTP header injection vulnerability

#### 2. Jackson Databind (CVE-2020-25649)
**Dependency**: `com.fasterxml.jackson.core:jackson-databind:2.9.10`
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.10</version> <!-- VULNERABLE: Should be 2.12.1+ -->
</dependency>
```
**Vulnerability**: Deserialization of untrusted data

#### 3. Apache Commons Collections (CVE-2015-6420)
**Dependency**: `org.apache.commons:commons-collections4:4.0`
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.0</version> <!-- VULNERABLE: Should be 4.1+ -->
</dependency>
```
**Vulnerability**: Unsafe deserialization

#### 4. Frontend: Axios (CVE-2021-3749)
**Dependency**: `axios:0.21.1` (in package.json)
```json
{
  "dependencies": {
    "axios": "0.21.1"  // VULNERABLE: Should be 0.21.2+
  }
}
```
**Vulnerability**: SSRF vulnerability

---

### 📊 Test Coverage Issues

#### 1. Missing Branch Coverage
- `NotificationService.sendNotification()` - Only happy path tested
- `WebhookService.retryWebhook()` - No test for retry logic
- `NotificationService.processNotificationQueue()` - No test for error scenarios

#### 2. Missing Edge Case Tests
- Null user in notification
- Invalid webhook URL
- Network timeout scenarios
- Concurrent notification processing

#### 3. Skeleton Tests
```java
@Test
public void testSendNotification() {
    // TODO: Add test implementation
    // No assertions
}
```

---

### 📋 Duplications

#### 1. Similar Notification Sending Logic
- Email, SMS, and Push notifications all have similar structure
- Webhook retry logic duplicated in multiple methods
- Validation logic repeated across controllers

#### 2. Frontend Component Duplication
- Similar form validation in multiple React components
- Duplicated API call patterns
- Repeated error handling code

---

### 💰 Technical Debt

#### 1. Deprecated API Usage
```java
// DEPRECATED: Using Date instead of LocalDateTime
Date orderDate = new Date();

// DEPRECATED: Using Vector instead of ArrayList
Vector<Notification> queue = new Vector<>();
```

#### 2. TODO Comments Indicating Technical Debt
```java
// TODO: Refactor this method - it's too complex
// TODO: Replace hardcoded values with configuration
// TODO: Add proper error handling
// TODO: Implement retry mechanism properly
```

#### 3. Code Complexity
- Methods with cyclomatic complexity > 15
- Classes with too many responsibilities
- Deeply nested conditionals

---

## Implementation

### Backend Components
- `model/Notification.java` - Notification entity
- `model/NotificationPreferences.java` - User notification preferences
- `model/WebhookConfiguration.java` - Webhook endpoint configuration
- `model/NotificationHistory.java` - Notification delivery history
- `service/NotificationService.java` - Core notification service (370+ lines, god class)
- `service/WebhookService.java` - Webhook delivery service
- `controller/NotificationController.java` - REST API endpoints
- `repository/NotificationRepository.java` - Notification data access
- `repository/NotificationRepositoryCustomImpl.java` - Custom SQL queries (with vulnerabilities)
- `repository/WebhookConfigurationRepository.java` - Webhook configuration data access
- `dto/NotificationDto.java` - Notification data transfer object
- `dto/WebhookDto.java` - Webhook data transfer object

### REST API Endpoints
- `POST /api/v1/notifications` - Create notification (XSS vulnerability)
- `GET /api/v1/notifications` - Get notifications (SQL injection)
- `GET /api/v1/notifications/history` - Get history (SQL injection)
- `GET /api/v1/notifications/user/{userId}` - Get notifications by user
- `POST /api/v1/webhooks` - Register webhook (SSRF vulnerability)
- `GET /api/v1/webhooks` - Get all webhooks
- `GET /api/v1/webhooks/{id}/test` - Test webhook (SSRF vulnerability)

### Frontend Components
- `components/NotificationPreferences.tsx` - User notification preferences UI
- `components/WebhookManagement.tsx` - Webhook management UI
- `components/NotificationHistory.tsx` - Notification history display
- `services/notificationService.ts` - Frontend notification API client
- `services/webhookService.ts` - Frontend webhook API client
- `types/notification.ts` - TypeScript type definitions

### Dependencies
- Apache HttpClient 4.5.6 (CVE-2020-13956) - Intentionally vulnerable
- Jackson Databind 2.9.10 (CVE-2020-25649) - Intentionally vulnerable
- Commons Collections 4.0 (CVE-2015-6420) - Intentionally vulnerable
- Axios 0.21.1 (CVE-2021-3749) - Intentionally vulnerable

### Test Files
- `NotificationServiceTest.java` - Skeleton tests (incomplete coverage)
- `WebhookServiceTest.java` - Skeleton tests (incomplete coverage)
- `NotificationPreferences.test.tsx` - Skeleton tests
- `WebhookManagement.test.tsx` - Skeleton tests

---


---

## Summary of Issues by Category

| Category | Count | Examples |
|----------|-------|----------|
| **Security Vulnerabilities** | 8 | SQL Injection, SSRF, Command Injection, Insecure Deserialization, XSS, Hardcoded Credentials, Weak Cryptography, Sensitive Data Exposure |
| **Bugs / Reliability** | 6 | NPE, Resource Leaks, Array Index OOB, Swallowed Exceptions, Race Conditions, Stale Closures |
| **Code Smells** | 9 | Duplicated Code, Complex Methods, Magic Numbers, Long Parameters, Dead Code, God Class, 'any' Types, Console.log, TODO Comments |
| **Third-Party Vulnerabilities** | 4 | Apache HttpClient, Jackson, Commons Collections, Axios |
| **Test Coverage** | 3 | Missing Branch Coverage, Missing Edge Cases, Skeleton Tests |
| **Duplications** | 2 | Similar Notification Logic, Frontend Component Duplication |
| **Technical Debt** | 3 | Deprecated APIs, TODO Comments, Code Complexity |

**Total: 35+ intentional issues across all major SonarQube categories**

---

## Integration with Existing Features

This feature integrates with:
- **OrderService**: Triggers notifications on order creation and processing events
- **UserService**: Retrieves user information for notifications
- **EmailService**: Reuses email infrastructure
- **ActivityLogService**: Can be extended to log notification events

---

## Important Notes

⚠️ **All issues documented in this feature are intentional** for SonarQube demonstration purposes:
- Security vulnerabilities are **intentionally included** to showcase SonarQube's security analysis
- Code smells and maintainability issues are **intentional** to demonstrate code quality analysis
- Vulnerable dependencies are **intentionally outdated** versions
- Test coverage is **intentionally incomplete** to show coverage analysis
- These issues should **NOT be fixed** as they serve the educational/demonstration purpose of this project

The feature provides legitimate business functionality while containing intentional issues that SonarQube can detect and report.

