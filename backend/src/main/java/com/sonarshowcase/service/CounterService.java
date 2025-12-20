package com.sonarshowcase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Counter service with race conditions.
 * 
 * REL-05: Race condition - non-atomic operations on shared state
 * MNT: Part of 6-level circular dependency cycle (architecture violation)
 * MNT: Also part of additional cycle with PaymentService
 * 
 * @author SonarShowcase
 */
@Service
public class CounterService {
    
    /**
     * Default constructor for CounterService.
     */
    public CounterService() {
    }

    // MNT: Part of 6-level cycle: ... -> CategoryService -> CounterService -> ActivityLogService -> ...
    @Autowired
    @org.springframework.context.annotation.Lazy
    private ActivityLogService activityLogService;
    
    // MNT: Additional cycle from PaymentService: PaymentService -> CounterService -> PaymentService
    @Autowired
    @org.springframework.context.annotation.Lazy
    private PaymentService paymentService;

    // REL: Non-thread-safe counter - race condition
    private int globalCounter = 0;
    
    // REL: Non-thread-safe map
    private Map<String, Integer> counters = new HashMap<>();
    
    // REL: Mutable static field - shared across all instances
    private static int staticCounter = 0;
    
    /**
     * REL-05: Race condition - increment is not atomic
     * Multiple threads can read-modify-write causing lost updates
     *
     * @return New counter value after increment
     */
    public int incrementGlobalCounter() {
        // REL: This is NOT atomic - race condition
        // Thread A reads 5, Thread B reads 5
        // Thread A writes 6, Thread B writes 6
        // Expected: 7, Actual: 6 (lost update)
        globalCounter = globalCounter + 1;
        return globalCounter;
    }
    
    /**
     * REL: Another race condition
     *
     * @param key Counter key to increment
     */
    public void incrementCounter(String key) {
        // REL: Check-then-act race condition
        if (counters.containsKey(key)) {
            int current = counters.get(key);
            // REL: Between get and put, another thread could modify
            counters.put(key, current + 1);
        } else {
            counters.put(key, 1);
        }
    }
    
    /**
     * REL: Static field modification - even worse
     *
     * @return New static counter value after increment
     */
    public static int incrementStaticCounter() {
        // REL: Modifying static field without synchronization
        staticCounter++;
        return staticCounter;
    }
    
    /**
     * REL: Compound operation race condition
     *
     * @return true if counter was decremented, false otherwise
     */
    public boolean decrementIfPositive() {
        // REL: Check-then-act is not atomic
        if (globalCounter > 0) {
            // REL: Another thread could have decremented between check and here
            globalCounter--;
            return true;
        }
        return false;
    }
    
    /**
     * REL: Reading without synchronization
     *
     * @return Current global counter value
     */
    public int getGlobalCounter() {
        // REL: Reading non-volatile field without sync
        return globalCounter;
    }
    
    /**
     * REL: Double-checked locking done wrong
     */
    private Object resource = null;
    
    /**
     * Gets the resource instance (not thread-safe)
     *
     * @return Resource object
     */
    public Object getResource() {
        if (resource == null) {
            // REL: Race condition - multiple threads could pass this check
            resource = new Object();
        }
        return resource;
    }
    
    /**
     * Reset counters - not thread safe
     */
    public void resetCounters() {
        // REL: Not atomic - could leave in inconsistent state
        globalCounter = 0;
        counters.clear();
        staticCounter = 0;
    }
    
    /**
     * MNT: Part of 6-level cycle - CounterService -> ActivityLogService -> ...
     * 
     * @param userId User ID to log activity for
     */
    public void logCounterActivity(Long userId) {
        // MNT: Using ActivityLogService creates dependency in 6-level cycle
        com.sonarshowcase.model.ActivityLog log = new com.sonarshowcase.model.ActivityLog();
        log.setUserId(userId);
        log.setAction("Counter incremented");
        activityLogService.createActivityLog(log);
    }
    
    /**
     * MNT: Additional cycle from PaymentService - CounterService -> PaymentService
     * 
     * @param orderId Order ID to process
     * @return true if payment processed
     */
    public boolean processPaymentForOrder(Long orderId) {
        // MNT: Using PaymentService creates additional cycle: PaymentService -> CounterService -> PaymentService
        incrementGlobalCounter();
        // This creates a circular dependency path
        return true;
    }
}

