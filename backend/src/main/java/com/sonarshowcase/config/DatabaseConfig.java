package com.sonarshowcase.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Database configuration with hardcoded credentials.
 * 
 * SEC-02: Hardcoded database credentials
 * 
 * @author SonarShowcase
 */
@Configuration
public class DatabaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);


    // SEC: Hardcoded credentials - SonarQube S2068
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/sonarshowcase";
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "admin123";
    
    // SEC: Another hardcoded password
    private static final String BACKUP_DB_PASSWORD = "backup_password_123!";
    
    /**
     * SEC: Hardcoded API tokens
     * Internal API token for authentication (intentionally insecure for demo)
     */
    public static final String INTERNAL_API_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ";
    
    // SEC: AWS credentials (fake but pattern matches)
    private static final String AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";


    /**
     * Creates a data source with hardcoded credentials
     * This should use environment variables instead
     *
     * @return Configured DataSource bean
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        
        // MNT: Debug logging with sensitive data
        logger.info("Connecting to database");
        logger.info("AWS configuration loaded, access key configured: {}", AWS_ACCESS_KEY != null);
        
        return dataSource;
    }
    
    // MNT: Unused method with hardcoded credentials
    private DataSource getBackupDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:postgresql://backup-server:5432/sonarshowcase");
        dataSource.setUsername("backup_admin");
        dataSource.setPassword(BACKUP_DB_PASSWORD);
        return dataSource;
    }
}

