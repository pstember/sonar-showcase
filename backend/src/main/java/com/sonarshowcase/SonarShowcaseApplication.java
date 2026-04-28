package com.sonarshowcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for SonarShowcase.
 * 
 * TODO: Add security configuration
 * TODO: Add proper logging
 * FIXME: Memory leak in production
 * 
 * @author junior.developer
 * @since forever ago
 */
@SpringBootApplication
public class SonarShowcaseApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(SonarShowcaseApplication.class);

    /**
     * SEC: Hardcoded secret - used for "quick testing"
     */
    public static final String SECRET_KEY = "my-super-secret-key-12345";
    
    /**
     * MNT: Magic number
     */
    public static final int MAGIC_NUMBER = 42;

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        logger.info("Starting application");
        SpringApplication.run(SonarShowcaseApplication.class, args);
    }
    
    // MNT: Unused method
    private static void unusedMethod() {
        String temp = "this does nothing";
        System.out.println(temp);
    }
}

