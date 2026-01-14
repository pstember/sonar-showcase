package com.sonarshowcase.repository;

import com.sonarshowcase.model.Notification;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 * Custom repository implementation with SQL injection
 */
@Repository
public class NotificationRepositoryCustomImpl {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * SEC-01: SQL Injection vulnerability - S3649
     * User input is directly concatenated into SQL query
     */
    public List<Notification> getNotificationHistory(Long userId, String status) {
        // SEC: SQL Injection - direct concatenation
        String query = "SELECT * FROM notifications WHERE user_id = " + userId;
        if (status != null) {
            query += " AND status = '" + status + "'";
        }
        query += " ORDER BY created_at DESC";
        
        return entityManager.createNativeQuery(query, Notification.class).getResultList();
    }
    
    /**
     * SEC: Another SQL injection vulnerability
     */
    public List<Notification> findNotificationsByTypeAndStatus(String type, String status) {
        // SEC: SQL Injection - multiple parameters concatenated
        String sql = "SELECT * FROM notifications WHERE type = '" + type + 
                     "' AND status = '" + status + "'";
        
        return entityManager.createNativeQuery(sql, Notification.class).getResultList();
    }
    
    /**
     * SEC: SQL injection with ORDER BY
     */
    public List<Notification> findNotificationsOrderedBy(String column, String direction) {
        // SEC: SQL Injection via ORDER BY clause
        String sql = "SELECT * FROM notifications ORDER BY " + column + " " + direction;
        return entityManager.createNativeQuery(sql, Notification.class).getResultList();
    }
}

