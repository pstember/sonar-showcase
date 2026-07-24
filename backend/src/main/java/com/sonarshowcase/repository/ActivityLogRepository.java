package com.sonarshowcase.repository;

import com.sonarshowcase.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 * ActivityLog repository with SQL injection vulnerability
 * 
 * SEC-01: SQL Injection via string concatenation in date range filtering
 * 
 * This repository provides methods to query activity logs with a clear
 * source-to-sink path for SQL injection vulnerability demonstration.
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>, JpaSpecificationExecutor<ActivityLog> {
    
    /**
     * Finds all activity logs for a specific user
     * 
     * @param userId The user ID
     * @return List of activity logs for the user
     */
    List<ActivityLog> findByUserId(Long userId);
}

/**
 * Custom repository implementation with SQL injection vulnerability
 * 
 * SEC-01: SQL Injection - User input directly concatenated into SQL query
 * This demonstrates a clear source-to-sink path for SonarQube analysis
 */
@Repository
class ActivityLogRepositoryCustomImpl {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * SEC-01: SQL Injection vulnerability in date range filtering
     * 
     * User input (startDate, endDate, userId) is directly concatenated into SQL query
     * without any sanitization or parameterization.
     * 
     * Attack example: 
     * startDate = "2025-01-01&#39; OR &#39;1&#39;=&#39;1&#39;--"
     * This will bypass the date filter and return all records
     * 
     * Another attack:
     * userId = "1&#39; UNION SELECT * FROM users WHERE role=&#39;ADMIN&#39;--"
     * This can extract sensitive user data
     * 
     * @param startDate Start date for filtering (vulnerable to SQL injection)
     * @param endDate End date for filtering (vulnerable to SQL injection)
     * @param userId Optional user ID filter (vulnerable to SQL injection)
     * @return List of activity logs matching the criteria
     */
    public List<ActivityLog> findByDateRange(String startDate, String endDate, String userId) {
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
     * SEC-01: Another SQL injection vulnerability in action filtering
     * 
     * @param action The action to filter by (vulnerable to SQL injection)
     * @return List of activity logs with the specified action
     */
    public List<ActivityLog> findByAction(String action) {
        // SEC: SQL Injection - direct string concatenation
        String sql = "SELECT * FROM activity_logs WHERE action = '" + action + "'";
        
        @SuppressWarnings("unchecked")
        List<ActivityLog> logs = entityManager.createNativeQuery(sql, ActivityLog.class).getResultList();
        
        return logs;
    }
}

