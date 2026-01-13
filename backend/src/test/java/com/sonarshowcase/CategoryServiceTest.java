package com.sonarshowcase;

import com.sonarshowcase.service.CategoryService;
import com.sonarshowcase.service.CounterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for CategoryService
 */
class CategoryServiceTest {

    private CategoryService categoryService;
    
    @Mock
    private CounterService counterService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService();
        
        // Use reflection to inject mock counter service
        try {
            java.lang.reflect.Field counterField = CategoryService.class.getDeclaredField("counterService");
            counterField.setAccessible(true);
            counterField.set(categoryService, counterService);
        } catch (Exception e) {
            // If reflection fails, tests will use real service
        }
    }
    
    @Test
    @DisplayName("Should calculate depth for category")
    void testCalculateDepth() {
        int depth = categoryService.calculateDepth("CAT001");
        assertTrue(depth > 0);
        assertTrue(depth <= 10);
    }
    
    @Test
    @DisplayName("Should calculate depth for different category IDs")
    void testCalculateDepth_differentIds() {
        int depth1 = categoryService.calculateDepth("CAT001");
        int depth2 = categoryService.calculateDepth("CAT002");
        int depth3 = categoryService.calculateDepth("CAT003");
        
        // All should return same depth (current implementation)
        assertEquals(depth1, depth2);
        assertEquals(depth2, depth3);
    }
    
    @Test
    @DisplayName("Should handle null category ID")
    void testCalculateDepth_nullId() {
        int depth = categoryService.calculateDepth(null);
        assertTrue(depth > 0);
    }
    
    @Test
    @DisplayName("Should handle empty category ID")
    void testCalculateDepth_emptyId() {
        int depth = categoryService.calculateDepth("");
        assertTrue(depth > 0);
    }
    
    @Test
    @DisplayName("Should get all subcategories")
    void testGetAllSubcategories() {
        List<String> subcategories = categoryService.getAllSubcategories("CAT001");
        
        assertNotNull(subcategories);
        assertFalse(subcategories.isEmpty());
        assertTrue(subcategories.contains("CAT001"));
        assertEquals(6, subcategories.size()); // 1 main + 5 sub
    }
    
    @Test
    @DisplayName("Should get subcategories for different category IDs")
    void testGetAllSubcategories_differentIds() {
        List<String> sub1 = categoryService.getAllSubcategories("CAT001");
        List<String> sub2 = categoryService.getAllSubcategories("CAT002");
        
        assertNotNull(sub1);
        assertNotNull(sub2);
        assertEquals(sub1.size(), sub2.size());
    }
    
    @Test
    @DisplayName("Should handle null category ID for subcategories")
    void testGetAllSubcategories_nullId() {
        List<String> subcategories = categoryService.getAllSubcategories(null);
        
        assertNotNull(subcategories);
        assertFalse(subcategories.isEmpty());
    }
    
    @Test
    @DisplayName("Should process category")
    void testProcessCategory() {
        // This method prints to console, so we just verify it doesn't throw
        assertDoesNotThrow(() -> categoryService.processCategory("CAT001"));
    }
    
    @Test
    @DisplayName("Should process category with null ID")
    void testProcessCategory_nullId() {
        assertDoesNotThrow(() -> categoryService.processCategory(null));
    }
    
    @Test
    @DisplayName("Should count items correctly")
    void testCountItems() {
        int count = categoryService.countItems(1, 5);
        assertEquals(4, count); // Should count from 1 to 4 (exclusive of 5)
    }
    
    @Test
    @DisplayName("Should return zero when current equals target")
    void testCountItems_currentEqualsTarget() {
        int count = categoryService.countItems(5, 5);
        assertEquals(0, count);
    }
    
    @Test
    @DisplayName("Should return zero when current is greater than target")
    void testCountItems_currentGreaterThanTarget() {
        int count = categoryService.countItems(10, 5);
        assertEquals(0, count);
    }
    
    @Test
    @DisplayName("Should return zero when current is zero or negative")
    void testCountItems_zeroOrNegative() {
        assertEquals(0, categoryService.countItems(0, 5));
        assertEquals(0, categoryService.countItems(-1, 5));
    }
    
    @Test
    @DisplayName("Should build category path with all components")
    void testBuildCategoryPath_allComponents() {
        String path = categoryService.buildCategoryPath("child", "parent", "grandparent");
        assertEquals("grandparent/parent/child", path);
    }
    
    @Test
    @DisplayName("Should build category path without grandparent")
    void testBuildCategoryPath_noGrandparent() {
        String path = categoryService.buildCategoryPath("child", "parent", null);
        assertEquals("parent/child", path);
    }
    
    @Test
    @DisplayName("Should build category path without parent")
    void testBuildCategoryPath_noParent() {
        String path = categoryService.buildCategoryPath("child", null, "grandparent");
        assertEquals("grandparent", path);
    }
    
    @Test
    @DisplayName("Should build category path with only ID")
    void testBuildCategoryPath_onlyId() {
        String path = categoryService.buildCategoryPath("child", null, null);
        assertEquals("child", path);
    }
    
    @Test
    @DisplayName("Should handle empty strings in category path")
    void testBuildCategoryPath_emptyStrings() {
        String path1 = categoryService.buildCategoryPath("child", "", "grandparent");
        assertEquals("grandparent", path1);
        
        String path2 = categoryService.buildCategoryPath("", "parent", "grandparent");
        assertEquals("grandparent/parent", path2);
    }
    
    @Test
    @DisplayName("Should handle null ID in category path")
    void testBuildCategoryPath_nullId() {
        String path = categoryService.buildCategoryPath(null, "parent", "grandparent");
        assertEquals("grandparent/parent", path);
    }
    
    @Test
    @DisplayName("Should return empty string when all components are null")
    void testBuildCategoryPath_allNull() {
        String path = categoryService.buildCategoryPath(null, null, null);
        assertEquals("", path);
    }
    
    @Test
    @DisplayName("Should get category item count")
    void testGetCategoryItemCount() {
        when(counterService.getGlobalCounter()).thenReturn(42);
        
        int count = categoryService.getCategoryItemCount("CAT001");
        
        assertTrue(count >= 0);
        verify(counterService, times(1)).incrementCounter("category_CAT001");
        verify(counterService, times(1)).getGlobalCounter();
    }
    
    @Test
    @DisplayName("Should get category item count for different categories")
    void testGetCategoryItemCount_differentCategories() {
        when(counterService.getGlobalCounter()).thenReturn(10);
        
        int count1 = categoryService.getCategoryItemCount("CAT001");
        int count2 = categoryService.getCategoryItemCount("CAT002");
        
        verify(counterService, times(1)).incrementCounter("category_CAT001");
        verify(counterService, times(1)).incrementCounter("category_CAT002");
    }
    
    @Test
    @DisplayName("Should handle null category ID for item count")
    void testGetCategoryItemCount_nullId() {
        when(counterService.getGlobalCounter()).thenReturn(0);
        
        int count = categoryService.getCategoryItemCount(null);
        
        verify(counterService, times(1)).incrementCounter("category_null");
        assertTrue(count >= 0);
    }
}
