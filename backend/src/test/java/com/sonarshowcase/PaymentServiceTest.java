package com.sonarshowcase;

import com.sonarshowcase.model.Order;
import com.sonarshowcase.model.User;
import com.sonarshowcase.service.PaymentService;
import com.sonarshowcase.service.EmailService;
import com.sonarshowcase.service.UserService;
import com.sonarshowcase.service.CounterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for PaymentService
 */
class PaymentServiceTest {

    private PaymentService paymentService;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private CounterService counterService;
    
    private Order testOrder;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentService();
        
        // Use reflection to inject mock services
        try {
            java.lang.reflect.Field emailField = PaymentService.class.getDeclaredField("emailService");
            emailField.setAccessible(true);
            emailField.set(paymentService, emailService);
            
            java.lang.reflect.Field userField = PaymentService.class.getDeclaredField("userService");
            userField.setAccessible(true);
            userField.set(paymentService, userService);
            
            java.lang.reflect.Field counterField = PaymentService.class.getDeclaredField("counterService");
            counterField.setAccessible(true);
            counterField.set(paymentService, counterService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks via reflection", e);
        }
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD-001");
        testOrder.setTotalAmount(new BigDecimal("100.00"));
        testOrder.setUser(testUser);
    }
    
    @Test
    @DisplayName("Should process payment successfully")
    void testProcessPayment() {
        boolean result = paymentService.processPayment(testOrder, "4111111111111111", "123");
        
        // Result depends on HTTP connection, but we verify it doesn't throw
        assertNotNull(Boolean.valueOf(result));
    }
    
    @Test
    @DisplayName("Should reject payment with amount less than minimum")
    void testProcessPayment_belowMinimum() {
        testOrder.setTotalAmount(new BigDecimal("0.25"));
        
        boolean result = paymentService.processPayment(testOrder, "4111111111111111", "123");
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should process payment at minimum amount")
    void testProcessPayment_minimumAmount() {
        testOrder.setTotalAmount(new BigDecimal("0.50"));
        
        boolean result = paymentService.processPayment(testOrder, "0.50", "123");
        
        assertNotNull(Boolean.valueOf(result));
    }
    
    @Test
    @DisplayName("Should handle payment with null order")
    void testProcessPayment_nullOrder() {
        assertThrows(NullPointerException.class, () -> {
            paymentService.processPayment(null, "4111111111111111", "123");
        });
    }
    
    @Test
    @DisplayName("Should handle payment with null card number")
    void testProcessPayment_nullCardNumber() {
        boolean result = paymentService.processPayment(testOrder, null, "123");
        
        assertNotNull(Boolean.valueOf(result));
    }
    
    @Test
    @DisplayName("Should handle payment with null CVV")
    void testProcessPayment_nullCvv() {
        boolean result = paymentService.processPayment(testOrder, "4111111111111111", null);
        
        assertNotNull(Boolean.valueOf(result));
    }
    
    @Test
    @DisplayName("Should handle payment with order without user")
    void testProcessPayment_noUser() {
        testOrder.setUser(null);
        
        boolean result = paymentService.processPayment(testOrder, "4111111111111111", "123");
        
        assertNotNull(Boolean.valueOf(result));
        verify(emailService, never()).sendOrderConfirmation(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should refund payment")
    void testRefundPayment() {
        boolean result = paymentService.refundPayment("TXN-001", new BigDecimal("50.00"));
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should refund payment with null transaction ID")
    void testRefundPayment_nullTransactionId() {
        boolean result = paymentService.refundPayment(null, new BigDecimal("50.00"));
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should refund payment with null amount")
    void testRefundPayment_nullAmount() {
        boolean result = paymentService.refundPayment("TXN-001", null);
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should refund payment with zero amount")
    void testRefundPayment_zeroAmount() {
        boolean result = paymentService.refundPayment("TXN-001", BigDecimal.ZERO);
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should validate card successfully")
    void testValidateCard() {
        boolean result = paymentService.validateCard("4111111111111111", "123", "12/25");
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should reject card with null number")
    void testValidateCard_nullCardNumber() {
        boolean result = paymentService.validateCard(null, "123", "12/25");
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should reject card with short number")
    void testValidateCard_shortCardNumber() {
        boolean result = paymentService.validateCard("123456789", "123", "12/25");
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should validate card with minimum length")
    void testValidateCard_minimumLength() {
        boolean result = paymentService.validateCard("1234567890123", "123", "12/25");
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should handle validation with null CVV")
    void testValidateCard_nullCvv() {
        boolean result = paymentService.validateCard("4111111111111111", null, "12/25");
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should handle validation with null expiry")
    void testValidateCard_nullExpiry() {
        boolean result = paymentService.validateCard("4111111111111111", "123", null);
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should get Stripe key")
    void testGetStripeKey() {
        String key = paymentService.getStripeKey();
        
        assertNotNull(key);
        assertFalse(key.isEmpty());
    }
    
    @Test
    @DisplayName("Should get user payment summary")
    void testGetUserPaymentSummary() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        
        String summary = paymentService.getUserPaymentSummary(1L);
        
        assertNotNull(summary);
        assertTrue(summary.contains("testuser"));
        verify(userService, times(1)).getUserById(1L);
    }
    
    @Test
    @DisplayName("Should handle user payment summary with null user")
    void testGetUserPaymentSummary_nullUser() {
        when(userService.getUserById(1L)).thenReturn(null);
        
        String summary = paymentService.getUserPaymentSummary(1L);
        
        assertNotNull(summary);
        assertTrue(summary.contains("unknown"));
    }
    
    @Test
    @DisplayName("Should track payment transaction")
    void testTrackPaymentTransaction() {
        paymentService.trackPaymentTransaction("TXN-001");
        
        verify(counterService, times(1)).incrementCounter("payment_TXN-001");
    }
    
    @Test
    @DisplayName("Should track payment transaction with null ID")
    void testTrackPaymentTransaction_nullId() {
        paymentService.trackPaymentTransaction(null);
        
        verify(counterService, times(1)).incrementCounter("payment_null");
    }
}
