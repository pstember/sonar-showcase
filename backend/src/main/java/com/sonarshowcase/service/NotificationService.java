package com.sonarshowcase.service;

import com.sonarshowcase.model.Notification;
import com.sonarshowcase.model.NotificationHistory;
import com.sonarshowcase.model.NotificationPreferences;
import com.sonarshowcase.model.User;
import com.sonarshowcase.model.Order;
import com.sonarshowcase.repository.NotificationRepository;
import com.sonarshowcase.repository.NotificationRepositoryCustomImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * NotificationService - God class with multiple responsibilities and intentional issues
 * 
 * MNT: God class - 500+ lines, handles email, SMS, push, webhooks, preferences, history
 * SEC: Multiple security vulnerabilities
 * REL: Multiple reliability bugs
 * 
 * @author SonarShowcase
 */
@Service
public class NotificationService {
    
    /**
     * Default constructor for NotificationService.
     */
    public NotificationService() {
    }

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationRepositoryCustomImpl notificationRepositoryCustom;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    // SEC: Hardcoded API keys - S6418
    private static final String TWILIO_API_KEY = "AC1234567890abcdef";
    private static final String TWILIO_AUTH_TOKEN = "abcdef1234567890";
    private static final String SENDGRID_API_KEY = "SG.abcdef1234567890";
    private static final String FIREBASE_SERVER_KEY = "AAAA1234567890:APA91b...";
    
    // REL: Race condition - not thread-safe
    private int retryCount = 0;
    
    // MNT: Using deprecated Vector instead of ArrayList
    private Vector<Notification> notificationQueue = new Vector<>();
    
    // MNT: Dead code - unused method
    private void oldNotificationMethod() {
        System.out.println("Old implementation - never called");
    }
    
    /**
     * MNT: Long parameter list - S107
     * Sends a notification with many parameters
     */
    public void sendNotification(
            String type, String recipient, String subject, String content,
            String priority, String channel, Map<String, String> metadata,
            boolean retryOnFailure) {
        
        // REL: NPE risk - user might be null
        User user = userService.getUserById(1L); // MNT: Magic number
        String email = user.getEmail(); // BUG: NPE if user is null
        
        // SEC: Logging sensitive data - S4787
        System.out.println("Sending notification to: " + recipient);
        System.out.println("Content: " + content);
        System.out.println("Using API key: " + TWILIO_API_KEY);
        
        Notification notification = new Notification();
        notification.setType(type);
        notification.setRecipient(recipient);
        notification.setSubject(subject);
        notification.setContent(content); // SEC: XSS - no sanitization
        notification.setPriority(priority);
        notification.setChannel(channel);
        notification.setCreatedAt(new Date());
        notification.setStatus("pending");
        
        notificationRepository.save(notification);
    }
    
    /**
     * MNT: Duplicated code - similar to sendOrderShipped
     * Sends order confirmation notification
     */
    public void sendOrderConfirmation(User user, Order order) {
        String email = user.getEmail();
        String subject = "Order Confirmation";
        String body = "Your order " + order.getOrderNumber() + " has been confirmed.";
        sendEmail(email, subject, body);
    }
    
    /**
     * MNT: Duplicated code - similar to sendOrderConfirmation
     * Sends order shipped notification
     */
    public void sendOrderShipped(User user, Order order) {
        String email = user.getEmail();
        String subject = "Order Shipped";
        String body = "Your order " + order.getOrderNumber() + " has been shipped.";
        sendEmail(email, subject, body);
    }
    
    /**
     * MNT: Duplicated code - similar pattern
     * Sends order delivered notification
     */
    public void sendOrderDelivered(User user, Order order) {
        String email = user.getEmail();
        String subject = "Order Delivered";
        String body = "Your order " + order.getOrderNumber() + " has been delivered.";
        sendEmail(email, subject, body);
    }
    
    /**
     * MNT: Duplicated code - similar pattern
     * Sends payment confirmation
     */
    public void sendPaymentConfirmation(User user, Order order) {
        String email = user.getEmail();
        String subject = "Payment Confirmed";
        String body = "Payment for order " + order.getOrderNumber() + " has been confirmed.";
        sendEmail(email, subject, body);
    }
    
    /**
     * MNT: Duplicated code - similar pattern
     * Sends refund notification
     */
    public void sendRefundNotification(User user, Order order) {
        String email = user.getEmail();
        String subject = "Refund Processed";
        String body = "Refund for order " + order.getOrderNumber() + " has been processed.";
        sendEmail(email, subject, body);
    }
    
    /**
     * Sends email notification
     * SEC: Logging sensitive data
     */
    public void sendEmail(String email, String subject, String content) {
        // SEC: Logging sensitive data - S4787
        System.out.println("Sending email to: " + email + " with content: " + content);
        logger.info("Email sent with API key: " + SENDGRID_API_KEY);
        
        try {
            emailService.sendEmail(email, subject, content);
        } catch (Exception e) {
            // REL: Swallowed exception - S1181
        }
    }
    
    /**
     * SEC: Command injection vulnerability - S2083
     * Sends SMS notification
     */
    public void sendSMS(String phoneNumber, String message) {
        // SEC: Command injection via phone number
        String command = "sms-sender --to " + phoneNumber + " --message " + message;
        try {
            Runtime.getRuntime().exec(command); // SEC: Command injection
        } catch (Exception e) {
            // REL: Swallowed exception
        }
    }
    
    /**
     * REL: Array index out of bounds - S2677
     * Parses phone number
     */
    public String parsePhoneNumber(String phoneNumber) {
        // BUG: No bounds checking
        String[] parts = phoneNumber.split("-");
        String countryCode = parts[0];
        String number = parts[1]; // Could throw ArrayIndexOutOfBoundsException
        return countryCode + "-" + number;
    }
    
    /**
     * MNT: Complex method - cyclomatic complexity > 15 - S3776
     * Processes notification queue
     */
    public void processNotificationQueue() {
        List<Notification> queue = notificationRepository.findByStatus("pending");
        
        for (Notification n : queue) {
            if (n.getType().equals("email")) {
                if (n.getPriority() != null && Integer.parseInt(n.getPriority()) > 5) {
                    if (isBusinessHours()) {
                        if (n.getChannel() != null && n.getChannel().equals("immediate")) {
                            sendEmail(n.getRecipient(), n.getSubject(), n.getContent());
                            n.setStatus("sent");
                        } else {
                            if (n.getRetryCount() != null && n.getRetryCount() < 3) {
                                retryNotification(n);
                            } else {
                                n.setStatus("failed");
                            }
                        }
                    } else {
                        if (n.getPriority() != null && Integer.parseInt(n.getPriority()) > 10) {
                            sendEmail(n.getRecipient(), n.getSubject(), n.getContent());
                            n.setStatus("sent");
                        } else {
                            n.setStatus("queued");
                        }
                    }
                } else {
                    if (n.getRetryCount() != null && n.getRetryCount() < 3) {
                        retryNotification(n);
                    } else {
                        n.setStatus("failed");
                    }
                }
            } else if (n.getType().equals("sms")) {
                if (n.getPriority() != null && Integer.parseInt(n.getPriority()) > 5) {
                    sendSMS(n.getRecipient(), n.getContent());
                    n.setStatus("sent");
                } else {
                    n.setStatus("queued");
                }
            } else if (n.getType().equals("push")) {
                sendPushNotification(n.getRecipient(), n.getContent());
                n.setStatus("sent");
            }
            
            notificationRepository.save(n);
        }
    }
    
    /**
     * MNT: Magic numbers - S109
     * Checks if current time is business hours
     */
    private boolean isBusinessHours() {
        Date now = new Date();
        int hour = now.getHours();
        // MNT: Magic numbers - what do 9 and 17 mean?
        return hour >= 9 && hour < 17;
    }
    
    /**
     * REL: Race condition - not thread-safe - S2886
     * Increments retry count
     */
    public void incrementRetryCount() {
        retryCount++; // REL: Race condition in multi-threaded environment
    }
    
    /**
     * REL: Resource leak - connection not closed - S2095
     * Retries notification
     */
    public void retryNotification(Notification notification) {
        try {
            // REL: Connection not closed in all code paths
            URL url = new URL("http://api.notification-service.com/retry");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.getOutputStream().write(notification.getContent().getBytes());
            // Missing: conn.disconnect() in finally block
        } catch (Exception e) {
            // REL: Swallowed exception
        }
    }
    
    /**
     * SEC: SQL Injection via repository - S3649
     * Gets notification history with SQL injection vulnerability
     */
    public List<Notification> getNotificationHistory(Long userId, String status) {
        // SEC: SQL Injection - passes user input directly to vulnerable repository method
        return notificationRepositoryCustom.getNotificationHistory(userId, status);
    }
    
    /**
     * Sends push notification
     */
    public void sendPushNotification(String deviceToken, String message) {
        // SEC: Logging sensitive data
        System.out.println("Sending push to device: " + deviceToken);
        System.out.println("Using Firebase key: " + FIREBASE_SERVER_KEY);
    }
    
    /**
     * SEC: Weak cryptography - MD5 - S4790
     * Signs webhook payload
     */
    public String signWebhook(String payload) {
        try {
            // SEC: Using MD5 for webhook signature
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(payload.getBytes());
            String signature = new String(hash);
            return signature;
        } catch (Exception e) {
            // REL: Swallowed exception
            return "";
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
     * Gets user notification preferences
     */
    public NotificationPreferences getUserPreferences(Long userId) {
        // TODO: Implement proper preference retrieval
        // FIXME: This is a temporary workaround
        return null;
    }
    
    /**
     * Saves notification to history
     */
    public void saveNotificationHistory(Notification notification) {
        NotificationHistory history = new NotificationHistory();
        history.setNotificationId(notification.getId());
        history.setUserId(notification.getUserId());
        history.setType(notification.getType());
        history.setStatus(notification.getStatus());
        history.setSentAt(notification.getSentAt());
        history.setCreatedAt(new Date());
        // TODO: Save to repository
    }
    
    // MNT: Dead code - unreachable (commented out to allow compilation)
    public void sendNotification() {
        return;
        // System.out.println("This never executes"); // MNT: Unreachable code - dead code
    }
    
    // MNT: Missing logger declaration
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NotificationService.class.getName());
}

