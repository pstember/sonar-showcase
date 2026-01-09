package com.sonarshowcase.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * NotificationPreferences entity - stores user notification preferences
 * 
 * @author SonarShowcase
 */
@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {
    
    /**
     * Default constructor for NotificationPreferences.
     */
    public NotificationPreferences() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true)
    private Long userId;
    
    @Column(name = "email_enabled")
    private Boolean emailEnabled;
    
    @Column(name = "sms_enabled")
    private Boolean smsEnabled;
    
    @Column(name = "push_enabled")
    private Boolean pushEnabled;
    
    @Column(name = "order_confirmation")
    private Boolean orderConfirmation;
    
    @Column(name = "order_shipped")
    private Boolean orderShipped;
    
    @Column(name = "order_delivered")
    private Boolean orderDelivered;
    
    @Column(name = "promotional")
    private Boolean promotional;
    
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public Boolean getSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(Boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public Boolean getPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public Boolean getOrderConfirmation() {
        return orderConfirmation;
    }

    public void setOrderConfirmation(Boolean orderConfirmation) {
        this.orderConfirmation = orderConfirmation;
    }

    public Boolean getOrderShipped() {
        return orderShipped;
    }

    public void setOrderShipped(Boolean orderShipped) {
        this.orderShipped = orderShipped;
    }

    public Boolean getOrderDelivered() {
        return orderDelivered;
    }

    public void setOrderDelivered(Boolean orderDelivered) {
        this.orderDelivered = orderDelivered;
    }

    public Boolean getPromotional() {
        return promotional;
    }

    public void setPromotional(Boolean promotional) {
        this.promotional = promotional;
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

