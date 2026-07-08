package com.sonarshowcase.config;

import com.sonarshowcase.model.User;
import com.sonarshowcase.model.Product;
import com.sonarshowcase.model.ActivityLog;
import com.sonarshowcase.repository.UserRepository;
import com.sonarshowcase.repository.ActivityLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Calendar;

/**
 * Data initializer - creates sample data on startup.
 * 
 * MNT: This class does too many things (seeding different entity types)
 * 
 * @author SonarShowcase
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {
    
    /**
     * Default constructor for DataInitializer.
     */
    public DataInitializer() {
    }

    // MNT: Field injection instead of constructor injection
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
    // SEC: Hardcoded default passwords
    private static final String DEFAULT_PASSWORD = "password123";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    public void run(String... args) throws Exception {
        // MNT: Console output instead of proper logging
        log.info("=== Initializing Sample Data ===");
        
        // Check if data already exists
        if (userRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }
        
        // SEC: Creating users with weak passwords
        createSampleUsers();
        
        // Create sample activity logs
        createSampleActivityLogs();
        
        log.info("=== Sample Data Initialized ===");
    }
    
    private void createSampleUsers() {
        // SEC: Hardcoded credentials
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@sonarshowcase.com");
        admin.setPassword(ADMIN_PASSWORD); // SEC: Plain text password
        admin.setRole("ADMIN");
        admin.setActive(true);
        admin.setCreatedAt(new Date());
        userRepository.save(admin);
        log.info("Created admin user with password: " + ADMIN_PASSWORD);
        
        // MNT: Duplicated code pattern
        User user1 = new User();
        user1.setUsername("john.doe");
        user1.setEmail("john@example.com");
        user1.setPassword(DEFAULT_PASSWORD);
        user1.setRole("USER");
        user1.setActive(true);
        user1.setCreatedAt(new Date());
        userRepository.save(user1);
        
        User user2 = new User();
        user2.setUsername("jane.smith");
        user2.setEmail("jane@example.com");
        user2.setPassword(DEFAULT_PASSWORD);
        user2.setRole("USER");
        user2.setActive(true);
        user2.setCreatedAt(new Date());
        userRepository.save(user2);
        
        User user3 = new User();
        user3.setUsername("bob.wilson");
        user3.setEmail("bob@example.com");
        user3.setPassword(DEFAULT_PASSWORD);
        user3.setRole("USER");
        user3.setActive(false);
        user3.setCreatedAt(new Date());
        userRepository.save(user3);
        
        // MNT: Magic number
        log.info("Created 4 sample users");
    }
    
    private void createSampleActivityLogs() {
        // Get users to associate logs with
        User admin = userRepository.findByUsername("admin").orElse(null);
        User user1 = userRepository.findByUsername("john.doe").orElse(null);
        User user2 = userRepository.findByUsername("jane.smith").orElse(null);
        
        if (admin == null || user1 == null || user2 == null) {
            log.info("Users not found, skipping activity log creation");
            return;
        }
        
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        
        // Create activity logs for admin
        ActivityLog log1 = new ActivityLog();
        log1.setUserId(admin.getId());
        log1.setAction("LOGIN");
        log1.setDetails("User logged in successfully");
        log1.setTimestamp(now);
        log1.setIpAddress("192.168.1.100");
        activityLogRepository.save(log1);
        
        // Create activity logs with different timestamps
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        
        ActivityLog log2 = new ActivityLog();
        log2.setUserId(user1.getId());
        log2.setAction("PROFILE_UPDATE");
        log2.setDetails("User updated profile information");
        log2.setTimestamp(cal.getTime());
        log2.setIpAddress("192.168.1.101");
        activityLogRepository.save(log2);
        
        cal.add(Calendar.DAY_OF_MONTH, -2);
        
        ActivityLog log3 = new ActivityLog();
        log3.setUserId(user2.getId());
        log3.setAction("ORDER_CREATE");
        log3.setDetails("User created a new order");
        log3.setTimestamp(cal.getTime());
        log3.setIpAddress("192.168.1.102");
        activityLogRepository.save(log3);
        
        cal.add(Calendar.DAY_OF_MONTH, -5);
        
        ActivityLog log4 = new ActivityLog();
        log4.setUserId(admin.getId());
        log4.setAction("ADMIN_ACTION");
        log4.setDetails("Admin performed system configuration");
        log4.setTimestamp(cal.getTime());
        log4.setIpAddress("192.168.1.100");
        activityLogRepository.save(log4);
        
        cal.add(Calendar.DAY_OF_MONTH, -10);
        
        ActivityLog log5 = new ActivityLog();
        log5.setUserId(user1.getId());
        log5.setAction("PASSWORD_CHANGE");
        log5.setDetails("User changed password");
        log5.setTimestamp(cal.getTime());
        log5.setIpAddress("192.168.1.101");
        activityLogRepository.save(log5);
        
        log.info("Created 5 sample activity logs");
    }
}

