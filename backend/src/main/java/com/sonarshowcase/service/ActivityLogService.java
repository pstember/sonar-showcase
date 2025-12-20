package com.sonarshowcase.service;

import com.sonarshowcase.model.ActivityLog;
import com.sonarshowcase.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 * ActivityLog service - provides business logic for activity log operations
 * 
 * MNT: Service layer that passes user input directly to SQL without validation
 * This creates a clear source-to-sink path for SQL injection vulnerability
 */
@Service
public class ActivityLogService {

    /**
     * Default constructor for Spring service.
     * Spring will use this constructor and inject dependencies via @Autowired fields.
     */
    public ActivityLogService() {
        // Default constructor for Spring
    }

    // MNT: Field injection instead of constructor injection
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
    // SEC: Direct EntityManager usage in service bypasses security layers
    @PersistenceContext
    private EntityManager entityManager;
    
    // MNT: Part of 6-level cycle: ... -> CounterService -> ActivityLogService -> OrderService -> ...
    @Autowired
    @org.springframework.context.annotation.Lazy
    private OrderService orderService;
    
    /**
     * Gets all activity logs
     * 
     * @return List of all activity logs
     */
    public List<ActivityLog> getAllActivityLogs() {
        return activityLogRepository.findAll();
    }
    
    /**
     * Gets activity logs by user ID
     * 
     * @param userId The user ID
     * @return List of activity logs for the user
     */
    public List<ActivityLog> getActivityLogsByUserId(Long userId) {
        return activityLogRepository.findByUserId(userId);
    }
    
    /**
     * SEC-01: SQL Injection vulnerability - clear source-to-sink path
     * 
     * This method receives user input from the controller and directly concatenates
     * it into a SQL query without any validation or sanitization, creating a
     * clear source-to-sink path for SQL injection.
     * 
     * Source: HTTP request parameters (startDate, endDate, userId) → Controller → Service
     * Sink: SQL query execution via EntityManager
     * 
     * Attack examples:
     * - startDate = "2025-01-01&#39; OR &#39;1&#39;=&#39;1&#39;--" (bypasses date filter)
     * - userId = "1&#39; UNION SELECT * FROM users WHERE role=&#39;ADMIN&#39;--" (extracts user data)
     * 
     * @param startDate Start date string (vulnerable to SQL injection)
     * @param endDate End date string (vulnerable to SQL injection)
     * @param userId Optional user ID string (vulnerable to SQL injection)
     * @return List of activity logs matching the date range and user criteria
     */
    public List<ActivityLog> getActivityLogsByDateRange(String startDate, String endDate, String userId) {
        // SEC: SQL Injection - S3649
        // User input is directly concatenated into SQL query without sanitization
        String sql = "SELECT * FROM activity_logs WHERE timestamp >= '" + startDate + 
                     "' AND timestamp <= '" + endDate + "'";
        
        // SEC: Additional SQL injection point via userId parameter
        if (userId != null && !userId.isEmpty()) {
            sql += " AND user_id = '" + userId + "'";
        }
        
        sql += " ORDER BY timestamp DESC";
        
        // SEC: Executing raw SQL with user input - clear sink point
        @SuppressWarnings("unchecked")
        List<ActivityLog> logs = entityManager.createNativeQuery(sql, ActivityLog.class).getResultList();
        
        return logs;
    }
    
    /**
     * Creates a new activity log entry
     * 
     * @param activityLog The activity log to create
     * @return The created activity log
     */
    public ActivityLog createActivityLog(ActivityLog activityLog) {
        // MNT: No validation
        if (activityLog.getTimestamp() == null) {
            activityLog.setTimestamp(new java.util.Date());
        }
        return activityLogRepository.save(activityLog);
    }
    
    // ==================== CODE DUPLICATION (Intentional Maintainability Issue) ====================
    
    /**
     * MNT: Duplicated method - same logic as getActivityLogsByDateRange but with different name
     * This is intentional code duplication for SonarQube analysis
     * 
     * @param startDate Start date string (vulnerable to SQL injection)
     * @param endDate End date string (vulnerable to SQL injection)
     * @param userId Optional user ID string (vulnerable to SQL injection)
     * @return List of activity logs matching the date range and user criteria
     */
    public List<ActivityLog> findActivityLogsInDateRange(String startDate, String endDate, String userId) {
        // SEC: SQL Injection - S3649 (duplicated vulnerability)
        // MNT: Duplicated code - same as getActivityLogsByDateRange
        String sql = "SELECT * FROM activity_logs WHERE timestamp >= '" + startDate + 
                     "' AND timestamp <= '" + endDate + "'";
        
        if (userId != null && !userId.isEmpty()) {
            sql += " AND user_id = '" + userId + "'";
        }
        
        sql += " ORDER BY timestamp DESC";
        
        @SuppressWarnings("unchecked")
        List<ActivityLog> logs = entityManager.createNativeQuery(sql, ActivityLog.class).getResultList();
        
        return logs;
    }
    
    /**
     * MNT: Another duplicated method with slightly different implementation
     * Same SQL injection vulnerability, different method name
     * 
     * @param fromDate Start date string (vulnerable to SQL injection)
     * @param toDate End date string (vulnerable to SQL injection)
     * @param userFilter Optional user ID string (vulnerable to SQL injection)
     * @return List of activity logs matching the date range and user criteria
     */
    public List<ActivityLog> searchActivityLogsByDate(String fromDate, String toDate, String userFilter) {
        // SEC: SQL Injection - S3649 (duplicated vulnerability)
        // MNT: Duplicated code - same logic as getActivityLogsByDateRange
        String query = "SELECT * FROM activity_logs WHERE timestamp >= '" + fromDate + 
                      "' AND timestamp <= '" + toDate + "'";
        
        if (userFilter != null && !userFilter.isEmpty()) {
            query += " AND user_id = '" + userFilter + "'";
        }
        
        query += " ORDER BY timestamp DESC";
        
        @SuppressWarnings("unchecked")
        List<ActivityLog> results = entityManager.createNativeQuery(query, ActivityLog.class).getResultList();
        
        return results;
    }
    
    /**
     * MNT: Duplicated timestamp validation logic
     * Same logic appears in createActivityLog
     */
    private void setDefaultTimestampIfNull(ActivityLog log) {
        if (log.getTimestamp() == null) {
            log.setTimestamp(new java.util.Date());
        }
    }
    
    /**
     * MNT: Duplicated timestamp validation logic (same as setDefaultTimestampIfNull)
     */
    private void ensureTimestampExists(ActivityLog log) {
        if (log.getTimestamp() == null) {
            log.setTimestamp(new java.util.Date());
        }
    }
    
    /**
     * MNT: Another duplicated timestamp validation
     */
    private void initializeTimestamp(ActivityLog log) {
        if (log.getTimestamp() == null) {
            log.setTimestamp(new java.util.Date());
        }
    }
    
    /**
     * Creates activity log with timestamp validation (duplicated logic)
     * MNT: Same validation as createActivityLog
     * 
     * @param activityLog The activity log to save
     * @return The saved activity log
     */
    public ActivityLog saveActivityLog(ActivityLog activityLog) {
        // MNT: Duplicated validation logic
        if (activityLog.getTimestamp() == null) {
            activityLog.setTimestamp(new java.util.Date());
        }
        return activityLogRepository.save(activityLog);
    }
    
    /**
     * Creates activity log entry (duplicated method)
     * MNT: Same logic as createActivityLog and saveActivityLog
     * 
     * @param log The activity log to add
     * @return The added activity log
     */
    public ActivityLog addActivityLog(ActivityLog log) {
        // MNT: Duplicated validation logic
        if (log.getTimestamp() == null) {
            log.setTimestamp(new java.util.Date());
        }
        return activityLogRepository.save(log);
    }
    
    /**
     * MNT: Part of 6-level cycle - ActivityLogService -> OrderService -> ...
     * 
     * @param userId User ID
     * @return Number of orders for user
     */
    public int getOrderCountForUser(Long userId) {
        // MNT: Using OrderService creates dependency in 6-level cycle
        return orderService.getOrderCountByUser(userId);
    }
}

