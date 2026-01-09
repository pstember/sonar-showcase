package com.sonarshowcase.repository;

import com.sonarshowcase.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Notification repository with SQL injection vulnerabilities
 * 
 * SEC-01: SQL Injection via string concatenation
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserId(Long userId);
    
    List<Notification> findByStatus(String status);
    
    List<Notification> findByType(String type);
}

