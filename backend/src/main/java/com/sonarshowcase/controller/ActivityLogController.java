package com.sonarshowcase.controller;

import com.sonarshowcase.dto.ActivityLogDto;
import com.sonarshowcase.model.ActivityLog;
import com.sonarshowcase.service.ActivityLogService;
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

/**
 * ActivityLog controller - REST endpoints for activity log management
 * 
 * SEC-01: SQL Injection vulnerability endpoint
 * This controller provides a clear source-to-sink path for SQL injection demonstration
 */
@RestController
@RequestMapping("/api/v1/activity-logs")
@Tag(name = "Activity Logs", description = "Activity log management API endpoints. ⚠️ Contains intentional SQL injection vulnerability for demonstration.")
public class ActivityLogController {

    /**
     * Default constructor for Spring controller.
     * Spring will use this constructor and inject dependencies via @Autowired fields.
     */
    public ActivityLogController() {
        // Default constructor for Spring
    }

    @Autowired
    private ActivityLogService activityLogService;

    /**
     * Get all activity logs
     * 
     * @return ResponseEntity containing a list of all activity logs
     */
    @Operation(summary = "Get all activity logs", description = "Returns all activity logs in the system")
    @ApiResponse(responseCode = "200", description = "List of all activity logs")
    @GetMapping
    public ResponseEntity<List<ActivityLog>> getAllActivityLogs() {
        return ResponseEntity.ok(activityLogService.getAllActivityLogs());
    }

    /**
     * SEC-01: SQL Injection vulnerability - clear source-to-sink path
     * 
     * VULNERABLE ENDPOINT - Demonstrates SQL Injection via date range filtering
     * 
     * This endpoint receives user input from HTTP request parameters and passes it
     * directly to the service layer, which then concatenates the values into a SQL
     * query without any sanitization or parameterization.
     * 
     * Source-to-Sink Path:
     * 1. HTTP Request Parameters (source) → Controller
     * 2. Controller → Service (getActivityLogsByDateRange)
     * 3. Service → SQL Query Construction (string concatenation)
     * 4. EntityManager.executeQuery() (sink)
     * 
     * Attack Examples:
     * - Bypass date filter: GET /api/v1/activity-logs/search?startDate=2025-01-01&#39; OR &#39;1&#39;=&#39;1&#39;--&amp;endDate=2025-12-31
     * - Extract user data: GET /api/v1/activity-logs/search?startDate=2025-01-01&amp;endDate=2025-12-31&amp;userId=1&#39; UNION SELECT id,username,email,password,role FROM users--
     * - Delete logs: GET /api/v1/activity-logs/search?startDate=2025-01-01&#39;; DELETE FROM activity_logs;--
     * 
     * @param startDate Start date for filtering (vulnerable to SQL injection)
     * @param endDate End date for filtering (vulnerable to SQL injection)
     * @param userId Optional user ID filter (vulnerable to SQL injection)
     * @return ResponseEntity containing a list of matching activity logs
     */
    @Operation(
        summary = "Search activity logs by date range (VULNERABLE)", 
        description = "🔴 SQL INJECTION VULNERABILITY - Intentional security issue for demonstration. " +
                     "User input is directly concatenated into SQL query without sanitization. " +
                     "Attack examples: startDate=2025-01-01&#39; OR &#39;1&#39;=&#39;1&#39;-- or userId=1&#39; UNION SELECT * FROM users--"
    )
    @ApiResponse(responseCode = "200", description = "List of activity logs (vulnerable to SQL injection)")
    @ApiResponse(responseCode = "500", description = "SQL error (may expose database structure)")
    @GetMapping("/search")
    @SuppressWarnings("all")
    public ResponseEntity<List<ActivityLog>> searchActivityLogs(
            @Parameter(description = "Start date (vulnerable to SQL injection)", example = "2025-01-01")
            @RequestParam String startDate,
            @Parameter(description = "End date (vulnerable to SQL injection)", example = "2025-12-31")
            @RequestParam String endDate,
            @Parameter(description = "Optional user ID filter (vulnerable to SQL injection)", example = "1")
            @RequestParam(required = false) String userId) {
        
        // SEC: User input passed directly to service without validation
        // Service will concatenate these values into SQL query
        List<ActivityLog> logs = activityLogService.getActivityLogsByDateRange(startDate, endDate, userId);
        
        return ResponseEntity.ok(logs);
    }

    /**
     * Get activity logs by user ID
     * 
     * @param userId The user ID
     * @return ResponseEntity containing a list of activity logs for the user
     */
    @Operation(summary = "Get activity logs by user ID", description = "Returns all activity logs for a specific user")
    @ApiResponse(responseCode = "200", description = "List of activity logs for the user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityLog>> getActivityLogsByUserId(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(activityLogService.getActivityLogsByUserId(userId));
    }

    /**
     * Create a new activity log entry
     * 
     * @param activityLog The activity log data
     * @return ResponseEntity containing the created activity log
     */
    @Operation(
        summary = "Create activity log", 
        description = "Creates a new activity log entry. ⚠️ MNT: No validation of activity log data"
    )
    @ApiResponse(responseCode = "200", description = "Activity log created")
    @PostMapping
    public ResponseEntity<ActivityLog> createActivityLog(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Activity log data", 
                required = true, 
                content = @Content(schema = @Schema(implementation = ActivityLogDto.class))
            )
            @RequestBody ActivityLogDto activityLogDto) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUserId(activityLogDto.getUserId());
        activityLog.setAction(activityLogDto.getAction());
        activityLog.setDetails(activityLogDto.getDetails());
        activityLog.setTimestamp(activityLogDto.getTimestamp());
        activityLog.setIpAddress(activityLogDto.getIpAddress());
        return ResponseEntity.ok(activityLogService.createActivityLog(activityLog));
    }
}

