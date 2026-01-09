package com.sonarshowcase.service;

import com.sonarshowcase.model.WebhookConfiguration;
import com.sonarshowcase.repository.WebhookConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

/**
 * WebhookService - handles webhook delivery with SSRF and other vulnerabilities
 * 
 * SEC: SSRF vulnerabilities, insecure deserialization
 * REL: Resource leaks, swallowed exceptions
 * 
 * @author SonarShowcase
 */
@Service
public class WebhookService {
    
    /**
     * Default constructor for WebhookService.
     */
    public WebhookService() {
    }

    @Autowired
    private WebhookConfigurationRepository webhookRepository;
    
    // SEC: Hardcoded webhook secret
    private static final String WEBHOOK_SECRET = "super_secret_webhook_key_12345";
    
    /**
     * SEC: Server-Side Request Forgery (SSRF) - S5131
     * Sends webhook to configured URL without validation
     */
    public boolean sendWebhook(WebhookConfiguration webhookConfig, String payload) {
        try {
            // SEC: SSRF - no validation of webhook URL
            // Attack vector: http://localhost:8080/admin or file:///etc/passwd
            URL webhookUrl = new URL(webhookConfig.getUrl());
            HttpURLConnection conn = (HttpURLConnection) webhookUrl.openConnection();
            
            // SEC: HTTP instead of HTTPS
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Webhook-Signature", signWebhook(payload));
            conn.setDoOutput(true);
            
            conn.getOutputStream().write(payload.getBytes());
            
            // REL: Resource leak - connection not closed
            int responseCode = conn.getResponseCode();
            
            return responseCode == 200;
        } catch (Exception e) {
            // REL: Swallowed exception - S1181
            return false;
        }
    }
    
    /**
     * SEC: SSRF - test webhook endpoint
     */
    public boolean testWebhook(String url, String payload) {
        try {
            // SEC: SSRF - user-controlled URL
            URL webhookUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) webhookUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.getOutputStream().write(payload.getBytes());
            // REL: Connection not closed
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            // REL: Swallowed exception
            return false;
        }
    }
    
    /**
     * SEC: Insecure deserialization - S5145
     * Processes webhook payload
     */
    public Object processWebhookPayload(byte[] payload) {
        try {
            // SEC: Deserializing untrusted data
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(payload));
            Object obj = ois.readObject(); // SEC: Insecure deserialization
            return obj;
        } catch (Exception e) {
            // REL: Swallowed exception
            return null;
        }
    }
    
    /**
     * SEC: Weak cryptography - MD5 - S4790
     * Signs webhook payload
     */
    public String signWebhook(String payload) {
        try {
            // SEC: Using MD5 for webhook signature
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest((payload + WEBHOOK_SECRET).getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // REL: Swallowed exception
            return "";
        }
    }
    
    /**
     * Retries webhook delivery
     */
    public void retryWebhook(WebhookConfiguration webhook, String payload) {
        // REL: Exception swallowed
        try {
            sendWebhook(webhook, payload);
        } catch (Exception e) {
            // Swallowed - no logging, no handling
        }
    }
    
    /**
     * Gets all active webhooks
     */
    public List<WebhookConfiguration> getActiveWebhooks() {
        return webhookRepository.findByActive(true);
    }
    
    /**
     * Saves webhook configuration
     */
    public WebhookConfiguration saveWebhook(WebhookConfiguration webhook) {
        webhook.setCreatedAt(new Date());
        webhook.setUpdatedAt(new Date());
        return webhookRepository.save(webhook);
    }
    
    /**
     * Triggers webhook for order event
     */
    public void triggerOrderWebhook(String eventType, String orderData) {
        List<WebhookConfiguration> webhooks = getActiveWebhooks();
        for (WebhookConfiguration webhook : webhooks) {
            if (webhook.getEventTypes().contains(eventType)) {
                sendWebhook(webhook, orderData);
            }
        }
    }
}

