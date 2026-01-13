package com.sonarshowcase;

import com.sonarshowcase.service.CounterService;
import com.sonarshowcase.service.ActivityLogService;
import com.sonarshowcase.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for CounterService
 * Note: This service has race conditions by design (for SonarQube demo)
 */
class CounterServiceTest {

    private CounterService counterService;
    
    @Mock
    private ActivityLogService activityLogService;
    
    @Mock
    private PaymentService paymentService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        counterService = new CounterService();
        
        // Use reflection to inject mock services
        try {
            java.lang.reflect.Field activityLogField = CounterService.class.getDeclaredField("activityLogService");
            activityLogField.setAccessible(true);
            activityLogField.set(counterService, activityLogService);
            
            java.lang.reflect.Field paymentField = CounterService.class.getDeclaredField("paymentService");
            paymentField.setAccessible(true);
            paymentField.set(counterService, paymentService);
        } catch (Exception e) {
            // If reflection fails, tests will use real services
        }
    }
    
    @Test
    @DisplayName("Should increment global counter")
    void testIncrementGlobalCounter() {
        int initial = counterService.getGlobalCounter();
        int result = counterService.incrementGlobalCounter();
        
        assertEquals(initial + 1, result);
        assertEquals(result, counterService.getGlobalCounter());
    }
    
    @Test
    @DisplayName("Should increment global counter multiple times")
    void testIncrementGlobalCounter_multiple() {
        int result1 = counterService.incrementGlobalCounter();
        int result2 = counterService.incrementGlobalCounter();
        int result3 = counterService.incrementGlobalCounter();
        
        assertEquals(result1 + 1, result2);
        assertEquals(result2 + 1, result3);
    }
    
    @Test
    @DisplayName("Should get global counter value")
    void testGetGlobalCounter() {
        int value = counterService.getGlobalCounter();
        assertTrue(value >= 0);
    }
    
    @Test
    @DisplayName("Should increment counter for key")
    void testIncrementCounter() {
        counterService.incrementCounter("test_key");
        
        // Verify no exceptions thrown
        assertDoesNotThrow(() -> counterService.incrementCounter("test_key"));
    }
    
    @Test
    @DisplayName("Should increment counter for multiple keys")
    void testIncrementCounter_multipleKeys() {
        counterService.incrementCounter("key1");
        counterService.incrementCounter("key2");
        counterService.incrementCounter("key1");
        
        // Verify no exceptions thrown
        assertDoesNotThrow(() -> counterService.incrementCounter("key3"));
    }
    
    @Test
    @DisplayName("Should handle null key")
    void testIncrementCounter_nullKey() {
        assertDoesNotThrow(() -> counterService.incrementCounter(null));
    }
    
    @Test
    @DisplayName("Should handle empty key")
    void testIncrementCounter_emptyKey() {
        assertDoesNotThrow(() -> counterService.incrementCounter(""));
    }
    
    @Test
    @DisplayName("Should increment static counter")
    void testIncrementStaticCounter() {
        int initial = CounterService.incrementStaticCounter();
        int result = CounterService.incrementStaticCounter();
        
        assertEquals(initial + 1, result);
    }
    
    @Test
    @DisplayName("Should increment static counter multiple times")
    void testIncrementStaticCounter_multiple() {
        int result1 = CounterService.incrementStaticCounter();
        int result2 = CounterService.incrementStaticCounter();
        int result3 = CounterService.incrementStaticCounter();
        
        assertEquals(result1 + 1, result2);
        assertEquals(result2 + 1, result3);
    }
    
    @Test
    @DisplayName("Should decrement if positive when counter is positive")
    void testDecrementIfPositive_positive() {
        counterService.incrementGlobalCounter();
        counterService.incrementGlobalCounter();
        
        boolean result = counterService.decrementIfPositive();
        
        assertTrue(result);
        assertTrue(counterService.getGlobalCounter() >= 0);
    }
    
    @Test
    @DisplayName("Should not decrement when counter is zero")
    void testDecrementIfPositive_zero() {
        counterService.resetCounters();
        
        boolean result = counterService.decrementIfPositive();
        
        assertFalse(result);
        assertEquals(0, counterService.getGlobalCounter());
    }
    
    @Test
    @DisplayName("Should get resource instance")
    void testGetResource() {
        Object resource1 = counterService.getResource();
        Object resource2 = counterService.getResource();
        
        assertNotNull(resource1);
        assertNotNull(resource2);
    }
    
    @Test
    @DisplayName("Should reset counters")
    void testResetCounters() {
        counterService.incrementGlobalCounter();
        counterService.incrementGlobalCounter();
        counterService.incrementCounter("test_key");
        
        counterService.resetCounters();
        
        assertEquals(0, counterService.getGlobalCounter());
    }
    
    @Test
    @DisplayName("Should reset counters multiple times")
    void testResetCounters_multiple() {
        counterService.incrementGlobalCounter();
        counterService.resetCounters();
        counterService.incrementGlobalCounter();
        counterService.resetCounters();
        
        assertEquals(0, counterService.getGlobalCounter());
    }
    
    @Test
    @DisplayName("Should log counter activity")
    void testLogCounterActivity() {
        Long userId = 123L;
        
        counterService.logCounterActivity(userId);
        
        verify(activityLogService, times(1)).createActivityLog(any());
    }
    
    @Test
    @DisplayName("Should log counter activity for different users")
    void testLogCounterActivity_differentUsers() {
        counterService.logCounterActivity(1L);
        counterService.logCounterActivity(2L);
        
        verify(activityLogService, times(2)).createActivityLog(any());
    }
    
    @Test
    @DisplayName("Should handle null user ID for activity logging")
    void testLogCounterActivity_nullUserId() {
        counterService.logCounterActivity(null);
        
        verify(activityLogService, times(1)).createActivityLog(any());
    }
    
    @Test
    @DisplayName("Should process payment for order")
    void testProcessPaymentForOrder() {
        Long orderId = 456L;
        
        boolean result = counterService.processPaymentForOrder(orderId);
        
        assertTrue(result);
        assertTrue(counterService.getGlobalCounter() > 0);
    }
    
    @Test
    @DisplayName("Should process payment for different orders")
    void testProcessPaymentForOrder_multiple() {
        boolean result1 = counterService.processPaymentForOrder(1L);
        boolean result2 = counterService.processPaymentForOrder(2L);
        
        assertTrue(result1);
        assertTrue(result2);
    }
    
    @Test
    @DisplayName("Should handle null order ID")
    void testProcessPaymentForOrder_nullId() {
        boolean result = counterService.processPaymentForOrder(null);
        
        assertTrue(result);
    }
}
