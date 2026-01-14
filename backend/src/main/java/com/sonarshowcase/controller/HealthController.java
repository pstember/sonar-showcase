package com.sonarshowcase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/**
 * Health check controller.
 * 
 * @author SonarShowcase
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Health check and system information endpoints")
public class HealthController {
    
    @Value("${app.version}")
    private String appVersion;
    
    /**
     * Default constructor for HealthController.
     */
    public HealthController() {
    }

    /**
     * Health check endpoint
     * MNT: Using raw HashMap instead of proper response class
     *
     * @return ResponseEntity containing health status information
     */
    // MNT: Using raw HashMap instead of proper response class
    @Operation(summary = "Health check", description = "Returns application health status")
    @ApiResponse(responseCode = "200", description = "Application is running")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", new Date());
        response.put("application", "SonarShowcase");
        response.put("version", "1.3.0"); // MNT: Hardcoded version
        
        // MNT: Debug output
        System.out.println("Health check called at: " + new Date());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * System information endpoint
     * SEC: Exposes internal information
     *
     * @return ResponseEntity containing system information (contains sensitive data)
     */
    // SEC: Exposes internal information
    @Operation(
        summary = "System information (INSECURE)", 
        description = "⚠️ SECURITY: Exposes sensitive system information including username, directory paths, and environment variables"
    )
    @ApiResponse(responseCode = "200", description = "System information (contains sensitive data)")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("java.version", System.getProperty("java.version"));
        info.put("os.name", System.getProperty("os.name"));
        info.put("user.name", System.getProperty("user.name")); // SEC: Exposes username
        info.put("user.dir", System.getProperty("user.dir")); // SEC: Exposes path
        
        // SEC: Exposing environment variables
        info.put("database.url", System.getenv("SPRING_DATASOURCE_URL"));
        
        return ResponseEntity.ok(info);
    }
}

