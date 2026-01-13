package com.sonarshowcase;

import com.sonarshowcase.util.OrderProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for OrderProcessor
 */
class OrderProcessorTest {

    private OrderProcessor orderProcessor;
    
    @BeforeEach
    void setUp() {
        orderProcessor = new OrderProcessor();
    }
    
    @Test
    @DisplayName("Should process order successfully")
    void testProcessOrder() {
        Map<String, Object> orderData = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("price", 100.0);
        item.put("quantity", 2);
        items.add(item);
        orderData.put("items", items);
        
        String result = orderProcessor.processOrder(
            orderData, "VIP", true, true, "US", 
            Collections.emptyList(), new Date()
        );
        
        assertNotNull(result);
        assertTrue(result.contains("Order processed"));
    }
    
    @Test
    @DisplayName("Should process order with null data")
    void testProcessOrder_nullData() {
        String result = orderProcessor.processOrder(
            null, "VIP", true, true, "US", 
            Collections.emptyList(), new Date()
        );
        
        assertEquals("Invalid order data", result);
    }
    
    @Test
    @DisplayName("Should process order without items")
    void testProcessOrder_noItems() {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("items", new ArrayList<>());
        
        String result = orderProcessor.processOrder(
            orderData, "VIP", true, true, "US", 
            Collections.emptyList(), new Date()
        );
        
        assertEquals("Invalid order data", result);
    }
    
    @Test
    @DisplayName("Should process order with VIP customer and priority")
    void testProcessOrder_vipPriority() {
        Map<String, Object> orderData = createValidOrderData();
        
        String result = orderProcessor.processOrder(
            orderData, "VIP", true, true, "US", 
            Collections.emptyList(), new Date()
        );
        
        assertNotNull(result);
        assertTrue(result.contains("Order processed"));
    }
    
    @Test
    @DisplayName("Should process order with GOLD customer")
    void testProcessOrder_goldCustomer() {
        Map<String, Object> orderData = createValidOrderData();
        
        String result = orderProcessor.processOrder(
            orderData, "GOLD", false, true, "US", 
            Collections.emptyList(), new Date()
        );
        
        assertNotNull(result);
        assertTrue(result.contains("Order processed"));
    }
    
    @Test
    @DisplayName("Should process order with SILVER customer")
    void testProcessOrder_silverCustomer() {
        Map<String, Object> orderData = createValidOrderData();
        
        String result = orderProcessor.processOrder(
            orderData, "SILVER", true, true, "US", 
            Collections.emptyList(), new Date()
        );
        
        assertNotNull(result);
        assertTrue(result.contains("Order processed"));
    }
    
    @Test
    @DisplayName("Should process order with EU region")
    void testProcessOrder_euRegion() {
        Map<String, Object> orderData = createValidOrderData();
        
        String result = orderProcessor.processOrder(
            orderData, "VIP", false, false, "EU", 
            Collections.emptyList(), new Date()
        );
        
        assertNotNull(result);
        assertTrue(result.contains("Order processed"));
    }
    
    @Test
    @DisplayName("Should process order with ASIA region")
    void testProcessOrder_asiaRegion() {
        Map<String, Object> orderData = createValidOrderData();
        
        String result = orderProcessor.processOrder(
            orderData, "VIP", false, false, "ASIA", 
            Collections.emptyList(), new Date()
        );
        
        assertNotNull(result);
        assertTrue(result.contains("Order processed"));
    }
    
    @Test
    @DisplayName("Should process order with coupon")
    void testProcessOrder_withCoupon() {
        Map<String, Object> orderData = createValidOrderData();
        List<String> coupons = Arrays.asList("PERCENT10", "FIXED5");
        
        String result = orderProcessor.processOrder(
            orderData, "VIP", false, false, "US", 
            coupons, new Date()
        );
        
        assertNotNull(result);
        assertTrue(result.contains("Order processed"));
    }
    
    @Test
    @DisplayName("Should validate order")
    void testValidateOrder() {
        Map<String, Object> order = createValidOrderForValidation();
        
        boolean result = orderProcessor.validateOrder(order);
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should reject null order")
    void testValidateOrder_null() {
        boolean result = orderProcessor.validateOrder(null);
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should reject order without customer")
    void testValidateOrder_noCustomer() {
        Map<String, Object> order = new HashMap<>();
        order.put("items", Arrays.asList(new HashMap<>()));
        
        boolean result = orderProcessor.validateOrder(order);
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should reject order without items")
    void testValidateOrder_noItems() {
        Map<String, Object> order = new HashMap<>();
        Map<String, Object> customer = new HashMap<>();
        customer.put("id", 1);
        customer.put("email", "test@example.com");
        order.put("customer", customer);
        
        boolean result = orderProcessor.validateOrder(order);
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should check order validity")
    void testCheckOrderValidity() {
        Map<String, Object> order = createValidOrderForValidation();
        
        boolean result = orderProcessor.checkOrderValidity(order);
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should check if order is valid")
    void testIsValidOrder() {
        Map<String, Object> order = createValidOrderForValidation();
        
        boolean result = orderProcessor.isValidOrder(order);
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should handle order")
    void testHandleOrder() {
        Map<String, Object> orderData = createValidOrderData();
        
        String result = orderProcessor.handleOrder(orderData, "VIP", true);
        
        assertNotNull(result);
        assertTrue(result.contains("Order handled"));
    }
    
    @Test
    @DisplayName("Should handle order with null data")
    void testHandleOrder_nullData() {
        String result = orderProcessor.handleOrder(null, "VIP", true);
        
        assertEquals("Invalid order data", result);
    }
    
    // Helper methods
    private Map<String, Object> createValidOrderData() {
        Map<String, Object> orderData = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("price", 100.0);
        item.put("quantity", 2);
        items.add(item);
        orderData.put("items", items);
        return orderData;
    }
    
    private Map<String, Object> createValidOrderForValidation() {
        Map<String, Object> order = new HashMap<>();
        
        Map<String, Object> customer = new HashMap<>();
        customer.put("id", 1);
        customer.put("email", "test@example.com");
        order.put("customer", customer);
        
        List<Map<String, Object>> items = new ArrayList<>();
        items.add(new HashMap<>());
        order.put("items", items);
        
        order.put("payment", new HashMap<>());
        order.put("shipping", new HashMap<>());
        
        return order;
    }
}
