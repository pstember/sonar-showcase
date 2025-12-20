package com.sonarshowcase.service;

import com.sonarshowcase.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Payment service with hardcoded API keys.
 * 
 * SEC-03: Hardcoded API keys and secrets
 * MNT: Circular dependency with EmailService (architecture violation)
 * 
 * @author SonarShowcase
 */
@Service
public class PaymentService {
    
    /**
     * Default constructor for PaymentService.
     */
    public PaymentService() {
    }

    // MNT: Circular dependency - PaymentService depends on EmailService
    // EmailService also depends on PaymentService (architecture violation)
    // Part of 6-level cycle: PaymentService -> EmailService -> CategoryService -> CounterService -> ActivityLogService -> OrderService -> PaymentService
    @Autowired
    @org.springframework.context.annotation.Lazy
    private EmailService emailService;
    
    // MNT: Additional cycle 1 from central PaymentService: PaymentService -> UserService -> PaymentService
    @Autowired
    @org.springframework.context.annotation.Lazy
    private UserService userService;
    
    // MNT: Additional cycle 2 from central PaymentService: PaymentService -> CounterService -> PaymentService
    // Note: CounterService is also part of the 6-level cycle, creating multiple dependency paths
    @Autowired
    @org.springframework.context.annotation.Lazy
    private CounterService counterService;

    // SEC: Hardcoded Stripe API keys - S6418
    // NOTE: These are intentionally fake keys for demo purposes (SonarQube security demo)
    private static final String STRIPE_SECRET_KEY = "sk_test_FAKE_KEY_FOR_DEMO_ONLY_1234567890";
    private static final String STRIPE_PUBLISHABLE_KEY = "pk_test_FAKE_KEY_FOR_DEMO_ONLY_1234567890";
    
    // SEC: Hardcoded PayPal credentials
    // NOTE: These are intentionally fake credentials for demo purposes (SonarQube security demo)
    private static final String PAYPAL_CLIENT_ID = "FAKE_PAYPAL_CLIENT_ID_FOR_DEMO_ONLY_1234567890";
    private static final String PAYPAL_SECRET = "FAKE_PAYPAL_SECRET_FOR_DEMO_ONLY_1234567890";
    
    // SEC: AWS credentials
    private static final String AWS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String AWS_SECRET = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    
    // SEC: Database password
    private static final String DB_PASSWORD = "super_secret_db_password_123!";

    /**
     * Process payment with hardcoded credentials.
     *
     * @param order Order to process payment for
     * @param cardNumber Credit card number
     * @param cvv Card verification value
     * @return true if payment processed successfully, false otherwise
     */
    public boolean processPayment(Order order, String cardNumber, String cvv) {
        // SEC: Logging sensitive payment info
        System.out.println("Processing payment with card: " + cardNumber + ", CVV: " + cvv);
        System.out.println("Using Stripe key: " + STRIPE_SECRET_KEY);
        
        BigDecimal amount = order.getTotalAmount();
        
        // MNT: Magic number for minimum payment
        if (amount.compareTo(new BigDecimal("0.50")) < 0) {
            return false;
        }
        
        try {
            // SEC: HTTP instead of HTTPS
            URL url = new URL("http://api.stripe.com/v1/charges");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            // SEC: Sending API key in insecure manner
            conn.setRequestProperty("Authorization", "Bearer " + STRIPE_SECRET_KEY);
            
            // REL: Not closing connection
            boolean success = conn.getResponseCode() == 200;
            
            // MNT: Circular dependency usage - PaymentService calling EmailService
            if (success && order.getUser() != null) {
                emailService.sendOrderConfirmation(order.getUser().getEmail(), order.getOrderNumber());
            }
            
            return success;
            
        } catch (Exception e) {
            // REL: Swallowing exception
            return false;
        }
    }
    
    /**
     * Refund payment.
     *
     * @param transactionId Transaction ID to refund
     * @param amount Amount to refund
     * @return true if refund processed successfully, false otherwise
     */
    public boolean refundPayment(String transactionId, BigDecimal amount) {
        // SEC: Logging with sensitive data
        System.out.println("Refunding " + amount + " for transaction: " + transactionId);
        System.out.println("PayPal Client ID: " + PAYPAL_CLIENT_ID);
        
        // MNT: Empty implementation
        return true;
    }
    
    /**
     * Validate card - insecure logging.
     *
     * @param cardNumber Credit card number to validate
     * @param cvv Card verification value
     * @param expiry Card expiry date
     * @return true if card is valid, false otherwise
     */
    public boolean validateCard(String cardNumber, String cvv, String expiry) {
        // SEC: Logging full card details - PCI DSS violation
        System.out.println("Validating card: " + cardNumber);
        System.out.println("CVV: " + cvv);
        System.out.println("Expiry: " + expiry);
        
        // MNT: Weak validation
        if (cardNumber == null || cardNumber.length() < 13) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get API keys - security leak.
     *
     * @return Stripe secret key
     */
    public String getStripeKey() {
        // SEC: Exposing secret key through method
        return STRIPE_SECRET_KEY;
    }
    
    /**
     * MNT: Additional cycle 1 usage - PaymentService -> UserService -> PaymentService
     * 
     * @param userId User ID to get payment info for
     * @return User payment summary
     */
    public String getUserPaymentSummary(Long userId) {
        // MNT: Using UserService creates additional cycle: PaymentService -> UserService -> PaymentService
        com.sonarshowcase.model.User user = userService.getUserById(userId);
        return "Payment info for user: " + (user != null ? user.getUsername() : "unknown");
    }
    
    /**
     * MNT: Additional cycle 2 usage - PaymentService -> CounterService -> PaymentService
     * 
     * @param transactionId Transaction ID
     */
    public void trackPaymentTransaction(String transactionId) {
        // MNT: Using CounterService creates additional cycle: PaymentService -> CounterService -> PaymentService
        counterService.incrementCounter("payment_" + transactionId);
    }
}

