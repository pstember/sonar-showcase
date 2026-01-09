package com.sonarshowcase.controller;

import com.sonarshowcase.dto.NotificationDto;
import com.sonarshowcase.dto.WebhookDto;
import com.sonarshowcase.model.Notification;
import com.sonarshowcase.model.WebhookConfiguration;
import com.sonarshowcase.service.NotificationService;
import com.sonarshowcase.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Notification controller with vulnerable endpoints
 * 
 * SEC: SQL Injection, XSS vulnerabilities
 * 
 * @author SonarShowcase
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notification management API endpoints. ⚠️ Contains intentional security vulnerabilities for demonstration.")
public class NotificationController {
    
    /**
     * Default constructor for NotificationController.
     */
    public NotificationController() {
    }

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private WebhookService webhookService;
    
    /**
     * SEC: XSS vulnerability - no sanitization of user input
     * Creates a new notification
     */
    @Operation(
        summary = "Create notification",
        description = "Creates a new notification. ⚠️ SECURITY: XSS vulnerability - user input not sanitized"
    )
    @ApiResponse(responseCode = "200", description = "Notification created")
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationDto notificationDto) {
        // SEC: XSS - no sanitization of user input
        Notification notification = new Notification();
        notification.setType(notificationDto.getType());
        notification.setRecipient(notificationDto.getRecipient());
        notification.setSubject(notificationDto.getSubject());
        notification.setContent(notificationDto.getContent()); // SEC: Could contain <script> tags
        notification.setPriority(notificationDto.getPriority());
        notification.setChannel(notificationDto.getChannel());
        
        // TODO: Add proper validation
        // FIXME: This is a temporary workaround
        
        return ResponseEntity.ok(notification);
    }
    
    /**
     * SEC: SQL Injection vulnerability - S3649
     * Gets notifications with SQL injection
     */
    @Operation(
        summary = "Get notifications",
        description = "Returns notifications. ⚠️ SECURITY: SQL Injection vulnerability - status parameter is vulnerable"
    )
    @ApiResponse(responseCode = "200", description = "List of notifications")
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @Parameter(description = "User ID", example = "1")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "Status (vulnerable to SQL injection)", example = "pending")
            @RequestParam(required = false) String status) {
        
        // SEC: SQL Injection - passes user input directly to vulnerable service method
        if (userId != null && status != null) {
            return ResponseEntity.ok(notificationService.getNotificationHistory(userId, status));
        }
        
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * SEC: SQL Injection vulnerability
     * Gets notification history
     */
    @Operation(
        summary = "Get notification history",
        description = "Returns notification history. ⚠️ SECURITY: SQL Injection vulnerability"
    )
    @ApiResponse(responseCode = "200", description = "List of notification history")
    @GetMapping("/history")
    public ResponseEntity<List<Notification>> getNotificationHistory(
            @Parameter(description = "User ID", example = "1")
            @RequestParam Long userId,
            @Parameter(description = "Status (vulnerable to SQL injection)", example = "sent")
            @RequestParam(required = false) String status) {
        
        // SEC: SQL Injection - user input directly in SQL query
        return ResponseEntity.ok(notificationService.getNotificationHistory(userId, status));
    }
    
    /**
     * Gets all notifications for a user
     */
    @Operation(summary = "Get notifications by user", description = "Returns all notifications for a specific user")
    @ApiResponse(responseCode = "200", description = "List of user's notifications")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId) {
        // TODO: Implement proper retrieval
        return ResponseEntity.ok(List.of());
    }
}

/**
 * Webhook controller with SSRF vulnerabilities
 */
@RestController
@RequestMapping("/api/v1/webhooks")
@Tag(name = "Webhooks", description = "Webhook management API endpoints. ⚠️ Contains intentional SSRF vulnerabilities for demonstration.")
class WebhookController {
    
    /**
     * Default constructor for WebhookController.
     */
    public WebhookController() {
    }

    @Autowired
    private WebhookService webhookService;
    
    /**
     * SEC: SSRF vulnerability - S5131
     * Registers a new webhook
     */
    @Operation(
        summary = "Register webhook",
        description = "Registers a new webhook endpoint. ⚠️ SECURITY: SSRF vulnerability - URL not validated"
    )
    @ApiResponse(responseCode = "200", description = "Webhook registered")
    @PostMapping
    public ResponseEntity<WebhookConfiguration> registerWebhook(@RequestBody WebhookDto webhookDto) {
        // SEC: SSRF - no URL validation
        WebhookConfiguration webhook = new WebhookConfiguration();
        webhook.setName(webhookDto.getName());
        webhook.setUrl(webhookDto.getUrl()); // SEC: Could be http://localhost:8080/admin
        webhook.setEventTypes(webhookDto.getEventTypes());
        webhook.setActive(webhookDto.getActive());
        
        return ResponseEntity.ok(webhookService.saveWebhook(webhook));
    }
    
    /**
     * SEC: SSRF vulnerability
     * Tests a webhook URL
     */
    @Operation(
        summary = "Test webhook",
        description = "Tests a webhook URL. ⚠️ SECURITY: SSRF vulnerability - URL not validated"
    )
    @ApiResponse(responseCode = "200", description = "Webhook test result")
    @GetMapping("/{id}/test")
    public ResponseEntity<Map<String, Object>> testWebhook(
            @Parameter(description = "Webhook ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Test URL (vulnerable to SSRF)", example = "http://example.com/webhook")
            @RequestParam(required = false) String url,
            @Parameter(description = "Test payload", example = "{\"test\": \"data\"}")
            @RequestParam(required = false) String payload) {
        
        // SEC: SSRF - user-controlled URL
        if (url != null && payload != null) {
            boolean success = webhookService.testWebhook(url, payload);
            return ResponseEntity.ok(Map.of("success", success, "url", url));
        }
        
        return ResponseEntity.ok(Map.of("success", false));
    }
    
    /**
     * Gets all webhooks
     */
    @Operation(summary = "Get all webhooks", description = "Returns all registered webhooks")
    @ApiResponse(responseCode = "200", description = "List of webhooks")
    @GetMapping
    public ResponseEntity<List<WebhookConfiguration>> getAllWebhooks() {
        return ResponseEntity.ok(webhookService.getActiveWebhooks());
    }
}

