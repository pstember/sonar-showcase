package com.sonarshowcase.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * WebhookConfiguration entity - stores webhook endpoint configurations
 * 
 * @author SonarShowcase
 */
@Entity
@Table(name = "webhook_configurations")
public class WebhookConfiguration {
    
    /**
     * Default constructor for WebhookConfiguration.
     */
    public WebhookConfiguration() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Column(length = 2000)
    private String url; // SEC: SSRF vulnerability - no validation
    
    @Column(name = "event_types")
    private String eventTypes; // MNT: Should be separate table or JSON
    
    @Column(name = "secret_key")
    private String secretKey; // SEC: Stored in plain text
    
    private Boolean active;
    
    @Column(name = "retry_count")
    private Integer retryCount;
    
    @Column(name = "timeout_seconds")
    private Integer timeoutSeconds;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Getters and Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(String eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

