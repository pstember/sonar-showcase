package com.sonarshowcase.repository;

import com.sonarshowcase.model.WebhookConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WebhookConfiguration repository
 * 
 * @author SonarShowcase
 */
@Repository
public interface WebhookConfigurationRepository extends JpaRepository<WebhookConfiguration, Long> {
    
    List<WebhookConfiguration> findByActive(Boolean active);
    
    List<WebhookConfiguration> findByName(String name);
}

