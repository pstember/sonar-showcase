package com.sonarshowcase;

import com.sonarshowcase.util.ReportBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for ReportBuilder
 */
class ReportBuilderTest {

    private ReportBuilder reportBuilder;
    
    @BeforeEach
    void setUp() {
        reportBuilder = new ReportBuilder();
    }
    
    @Test
    @DisplayName("Should build full report")
    void testBuildFullReport() {
        List<Map<String, Object>> data = createTestData();
        
        String report = reportBuilder.buildFullReport(
            data, "Test Report", "Test Title", "Test Author",
            true, true, true
        );
        
        assertNotNull(report);
        assertFalse(report.isEmpty());
        assertTrue(report.contains("Test Title"));
        assertTrue(report.contains("Test Author"));
    }
    
    @Test
    @DisplayName("Should build report without header")
    void testBuildFullReport_noHeader() {
        List<Map<String, Object>> data = createTestData();
        
        String report = reportBuilder.buildFullReport(
            data, "Test Report", "Test Title", "Test Author",
            false, true, true
        );
        
        assertNotNull(report);
        assertFalse(report.contains("SONARSHOWCASE REPORT"));
    }
    
    @Test
    @DisplayName("Should build report without footer")
    void testBuildFullReport_noFooter() {
        List<Map<String, Object>> data = createTestData();
        
        String report = reportBuilder.buildFullReport(
            data, "Test Report", "Test Title", "Test Author",
            true, false, true
        );
        
        assertNotNull(report);
        assertFalse(report.contains("END OF REPORT"));
    }
    
    @Test
    @DisplayName("Should build report without summary")
    void testBuildFullReport_noSummary() {
        List<Map<String, Object>> data = createTestData();
        
        String report = reportBuilder.buildFullReport(
            data, "Test Report", "Test Title", "Test Author",
            true, true, false
        );
        
        assertNotNull(report);
    }
    
    @Test
    @DisplayName("Should handle null data")
    void testBuildFullReport_nullData() {
        String report = reportBuilder.buildFullReport(
            null, "Test Report", "Test Title", "Test Author",
            true, true, true
        );
        
        assertNotNull(report);
        assertTrue(report.contains("No data available"));
    }
    
    @Test
    @DisplayName("Should handle empty data")
    void testBuildFullReport_emptyData() {
        String report = reportBuilder.buildFullReport(
            new ArrayList<>(), "Test Report", "Test Title", "Test Author",
            true, true, true
        );
        
        assertNotNull(report);
        assertTrue(report.contains("No data available"));
    }
    
    @Test
    @DisplayName("Should build report with numeric data")
    void testBuildFullReport_numericData() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("amount", 100.50);
        row.put("quantity", 5);
        data.add(row);
        
        String report = reportBuilder.buildFullReport(
            data, "Test Report", "Test Title", "Test Author",
            true, true, true
        );
        
        assertNotNull(report);
        assertTrue(report.contains("amount"));
    }
    
    @Test
    @DisplayName("Should build report with date data")
    void testBuildFullReport_dateData() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("date", new Date());
        data.add(row);
        
        String report = reportBuilder.buildFullReport(
            data, "Test Report", "Test Title", "Test Author",
            true, true, true
        );
        
        assertNotNull(report);
    }
    
    @Test
    @DisplayName("Should build report with list data")
    void testBuildFullReport_listData() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("items", Arrays.asList("item1", "item2"));
        data.add(row);
        
        String report = reportBuilder.buildFullReport(
            data, "Test Report", "Test Title", "Test Author",
            true, true, true
        );
        
        assertNotNull(report);
    }
    
    @Test
    @DisplayName("Should build simple report")
    void testBuildSimpleReport() {
        List<Map<String, Object>> data = createTestData();
        
        String report = reportBuilder.buildSimpleReport(data);
        
        assertNotNull(report);
        assertFalse(report.isEmpty());
        assertTrue(report.contains("Simple Report"));
    }
    
    @Test
    @DisplayName("Should build simple report with null data")
    void testBuildSimpleReport_nullData() {
        String report = reportBuilder.buildSimpleReport(null);
        
        assertNotNull(report);
        assertTrue(report.contains("Simple Report"));
    }
    
    @Test
    @DisplayName("Should build simple report with empty data")
    void testBuildSimpleReport_emptyData() {
        String report = reportBuilder.buildSimpleReport(new ArrayList<>());
        
        assertNotNull(report);
        assertTrue(report.contains("Simple Report"));
    }
    
    // Helper method
    private List<Map<String, Object>> createTestData() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("name", "Test Item");
        row.put("value", 100);
        data.add(row);
        return data;
    }
}
