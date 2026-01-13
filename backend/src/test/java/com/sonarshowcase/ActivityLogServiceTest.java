package com.sonarshowcase;

import com.sonarshowcase.model.ActivityLog;
import com.sonarshowcase.repository.ActivityLogRepository;
import com.sonarshowcase.service.ActivityLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for ActivityLogService to increase coverage.
 * MNT: Some tests have duplicated assertions (intentional for coverage)
 */
class ActivityLogServiceTest {

    private ActivityLogService activityLogService;
    
    @Mock
    private ActivityLogRepository activityLogRepository;
    
    private ActivityLog testLog;
    private List<ActivityLog> testLogs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activityLogService = new ActivityLogService();
        
        // Use reflection to inject mock repository
        try {
            java.lang.reflect.Field repoField = ActivityLogService.class.getDeclaredField("activityLogRepository");
            repoField.setAccessible(true);
            repoField.set(activityLogService, activityLogRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock via reflection", e);
        }
        
        testLog = new ActivityLog();
        testLog.setId(1L);
        testLog.setUserId(100L);
        testLog.setAction("LOGIN");
        testLog.setDetails("Test login");
        testLog.setTimestamp(new Date());
        testLog.setIpAddress("192.168.1.100");
        
        testLogs = new ArrayList<>();
        testLogs.add(testLog);
    }
    
    @Test
    @DisplayName("Should get all activity logs")
    void testGetAllActivityLogs() {
        when(activityLogRepository.findAll()).thenReturn(testLogs);
        
        List<ActivityLog> result = activityLogService.getAllActivityLogs();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(activityLogRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Should get activity logs by user ID")
    void testGetActivityLogsByUserId() {
        Long userId = 100L;
        when(activityLogRepository.findByUserId(userId)).thenReturn(testLogs);
        
        List<ActivityLog> result = activityLogService.getActivityLogsByUserId(userId);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(activityLogRepository, times(1)).findByUserId(userId);
    }
    
    @Test
    @DisplayName("Should create activity log with timestamp")
    void testCreateActivityLog_withTimestamp() {
        when(activityLogRepository.save(testLog)).thenReturn(testLog);
        
        ActivityLog result = activityLogService.createActivityLog(testLog);
        
        assertNotNull(result);
        assertNotNull(result.getTimestamp());
        verify(activityLogRepository, times(1)).save(testLog);
    }
    
    @Test
    @DisplayName("Should create activity log without timestamp and set default")
    void testCreateActivityLog_withoutTimestamp() {
        ActivityLog logWithoutTimestamp = new ActivityLog();
        logWithoutTimestamp.setUserId(100L);
        logWithoutTimestamp.setAction("TEST");
        logWithoutTimestamp.setTimestamp(null);
        
        when(activityLogRepository.save(any(ActivityLog.class))).thenAnswer(invocation -> {
            ActivityLog log = invocation.getArgument(0);
            log.setId(1L);
            return log;
        });
        
        ActivityLog result = activityLogService.createActivityLog(logWithoutTimestamp);
        
        assertNotNull(result);
        assertNotNull(result.getTimestamp());
        verify(activityLogRepository, times(1)).save(any(ActivityLog.class));
    }
    
    @Test
    @DisplayName("Should handle null activity log")
    void testCreateActivityLog_nullLog() {
        // This test may throw NPE, which is acceptable for demonstration
        try {
            activityLogService.createActivityLog(null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            // Expected behavior
        }
    }
    
    @Test
    @DisplayName("Should handle empty activity logs list")
    void testGetAllActivityLogs_emptyList() {
        when(activityLogRepository.findAll()).thenReturn(new ArrayList<>());
        
        List<ActivityLog> result = activityLogService.getAllActivityLogs();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(activityLogRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Should handle null user ID")
    void testGetActivityLogsByUserId_nullId() {
        when(activityLogRepository.findByUserId(null)).thenReturn(new ArrayList<>());
        
        List<ActivityLog> result = activityLogService.getActivityLogsByUserId(null);
        
        assertNotNull(result);
        verify(activityLogRepository, times(1)).findByUserId(null);
    }
    
    @Test
    @DisplayName("Should handle zero user ID")
    void testGetActivityLogsByUserId_zeroId() {
        when(activityLogRepository.findByUserId(0L)).thenReturn(new ArrayList<>());
        
        List<ActivityLog> result = activityLogService.getActivityLogsByUserId(0L);
        
        assertNotNull(result);
        verify(activityLogRepository, times(1)).findByUserId(0L);
    }
    
    // ==================== Duplicated Tests (Intentional Code Duplication) ====================
    
    @Test
    @DisplayName("Should get all activity logs (duplicated test)")
    void testGetAllActivityLogs_duplicate() {
        when(activityLogRepository.findAll()).thenReturn(testLogs);
        
        List<ActivityLog> result = activityLogService.getAllActivityLogs();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(activityLogRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Should create activity log with timestamp (duplicated test)")
    void testCreateActivityLog_withTimestamp_duplicate() {
        when(activityLogRepository.save(testLog)).thenReturn(testLog);
        
        ActivityLog result = activityLogService.createActivityLog(testLog);
        
        assertNotNull(result);
        assertNotNull(result.getTimestamp());
        verify(activityLogRepository, times(1)).save(testLog);
    }
    
    @Test
    @DisplayName("Should get activity logs by user ID (duplicated test)")
    void testGetActivityLogsByUserId_duplicate() {
        Long userId = 100L;
        when(activityLogRepository.findByUserId(userId)).thenReturn(testLogs);
        
        List<ActivityLog> result = activityLogService.getActivityLogsByUserId(userId);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(activityLogRepository, times(1)).findByUserId(userId);
    }
    
    // ==================== Edge Cases ====================
    
    @Test
    @DisplayName("Should handle multiple activity logs")
    void testGetAllActivityLogs_multipleLogs() {
        List<ActivityLog> multipleLogs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ActivityLog log = new ActivityLog();
            log.setId((long) i);
            log.setUserId(100L);
            log.setAction("ACTION_" + i);
            multipleLogs.add(log);
        }
        
        when(activityLogRepository.findAll()).thenReturn(multipleLogs);
        
        List<ActivityLog> result = activityLogService.getAllActivityLogs();
        
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(activityLogRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Should handle activity log with all fields null except required")
    void testCreateActivityLog_minimalFields() {
        ActivityLog minimalLog = new ActivityLog();
        minimalLog.setUserId(100L);
        minimalLog.setAction("MINIMAL");
        
        when(activityLogRepository.save(any(ActivityLog.class))).thenAnswer(invocation -> {
            ActivityLog log = invocation.getArgument(0);
            log.setId(1L);
            return log;
        });
        
        ActivityLog result = activityLogService.createActivityLog(minimalLog);
        
        assertNotNull(result);
        assertNotNull(result.getTimestamp());
    }
    
    @Test
    @DisplayName("Should preserve existing timestamp when creating activity log")
    void testCreateActivityLog_preserveTimestamp() {
        Date specificDate = new Date(1000000000L);
        testLog.setTimestamp(specificDate);
        
        when(activityLogRepository.save(testLog)).thenReturn(testLog);
        
        ActivityLog result = activityLogService.createActivityLog(testLog);
        
        assertNotNull(result);
        assertEquals(specificDate, result.getTimestamp());
    }
    
    @Test
    @DisplayName("Should handle very large user ID")
    void testGetActivityLogsByUserId_largeId() {
        Long largeUserId = Long.MAX_VALUE;
        when(activityLogRepository.findByUserId(largeUserId)).thenReturn(new ArrayList<>());
        
        List<ActivityLog> result = activityLogService.getActivityLogsByUserId(largeUserId);
        
        assertNotNull(result);
        verify(activityLogRepository, times(1)).findByUserId(largeUserId);
    }
    
    @Test
    @DisplayName("Should handle negative user ID")
    void testGetActivityLogsByUserId_negativeId() {
        Long negativeUserId = -1L;
        when(activityLogRepository.findByUserId(negativeUserId)).thenReturn(new ArrayList<>());
        
        List<ActivityLog> result = activityLogService.getActivityLogsByUserId(negativeUserId);
        
        assertNotNull(result);
        verify(activityLogRepository, times(1)).findByUserId(negativeUserId);
    }
}

