package com.sonarshowcase;

import com.sonarshowcase.util.BatchProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for BatchProcessor
 */
class BatchProcessorTest {

    private BatchProcessor batchProcessor;
    
    @BeforeEach
    void setUp() {
        batchProcessor = new BatchProcessor();
    }
    
    @Test
    @DisplayName("Should process items array")
    void testProcessItems() {
        String[] items = {"item1", "item2", "item3"};
        String[] result = batchProcessor.processItems(items);
        
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("ITEM1", result[0]);
        assertEquals("item2", result[1]);
        assertNotNull(result[2]);
    }
    
    @Test
    @DisplayName("Should handle null items array")
    void testProcessItems_null() {
        String[] result = batchProcessor.processItems(null);
        
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    @DisplayName("Should handle empty items array")
    void testProcessItems_empty() {
        String[] items = {};
        String[] result = batchProcessor.processItems(items);
        
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    @DisplayName("Should handle items array with less than 3 items")
    void testProcessItems_lessThanThree() {
        String[] items1 = {"item1"};
        String[] items2 = {"item1", "item2"};
        
        String[] result1 = batchProcessor.processItems(items1);
        String[] result2 = batchProcessor.processItems(items2);
        
        assertEquals(0, result1.length);
        assertEquals(0, result2.length);
    }
    
    @Test
    @DisplayName("Should handle items array with more than 3 items")
    void testProcessItems_moreThanThree() {
        String[] items = {"item1", "item2", "item3", "item4", "item5"};
        String[] result = batchProcessor.processItems(items);
        
        assertNotNull(result);
        assertEquals(5, result.length);
        assertEquals("ITEM1", result[0]);
    }
    
    @Test
    @DisplayName("Should sum array of integers")
    void testSumArray() {
        int[] numbers = {1, 2, 3, 4, 5};
        int sum = batchProcessor.sumArray(numbers);
        
        assertEquals(15, sum);
    }
    
    @Test
    @DisplayName("Should sum empty array")
    void testSumArray_empty() {
        int[] numbers = {};
        int sum = batchProcessor.sumArray(numbers);
        
        assertEquals(0, sum);
    }
    
    @Test
    @DisplayName("Should sum array with negative numbers")
    void testSumArray_negative() {
        int[] numbers = {-1, -2, 3, 4};
        int sum = batchProcessor.sumArray(numbers);
        
        assertEquals(4, sum);
    }
    
    @Test
    @DisplayName("Should sum array with zeros")
    void testSumArray_zeros() {
        int[] numbers = {0, 0, 0};
        int sum = batchProcessor.sumArray(numbers);
        
        assertEquals(0, sum);
    }
    
    @Test
    @DisplayName("Should get item from list with offset")
    void testGetItem() {
        List<String> items = new ArrayList<>();
        items.add("item1");
        items.add("item2");
        items.add("item3");
        
        // offset 1 means: index = size(3) - offset(1) = 2, so item3
        String result = batchProcessor.getItem(items, 1);
        
        assertEquals("item3", result);
    }
    
    @Test
    @DisplayName("Should get last item with offset 0")
    void testGetItem_lastItem() {
        List<String> items = new ArrayList<>();
        items.add("item1");
        items.add("item2");
        
        // offset 0 means: index = size(2) - offset(0) = 2, which is out of bounds
        // This test expects an exception
        assertThrows(IndexOutOfBoundsException.class, () -> {
            batchProcessor.getItem(items, 0);
        });
    }
    
    @Test
    @DisplayName("Should handle get item with invalid offset")
    void testGetItem_invalidOffset() {
        List<String> items = new ArrayList<>();
        items.add("item1");
        
        // This will throw IndexOutOfBoundsException
        assertThrows(IndexOutOfBoundsException.class, () -> {
            batchProcessor.getItem(items, 5);
        });
    }
    
    @Test
    @DisplayName("Should process batch")
    void testProcessBatch() {
        String[] batch = {"item1", "item2"};
        
        // This may throw NPE if batch becomes null
        assertDoesNotThrow(() -> {
            batchProcessor.processBatch(batch);
        });
    }
    
    @Test
    @DisplayName("Should handle empty batch")
    void testProcessBatch_empty() {
        String[] batch = {};
        
        // This will throw NPE
        assertThrows(NullPointerException.class, () -> {
            batchProcessor.processBatch(batch);
        });
    }
    
    @Test
    @DisplayName("Should copy arrays")
    void testCopyArrays() {
        String[] source = {"a", "b", "c"};
        String[] dest = new String[3];
        
        batchProcessor.copyArrays(source, dest);
        
        assertArrayEquals(source, dest);
    }
    
    @Test
    @DisplayName("Should handle copy arrays with different sizes")
    void testCopyArrays_differentSizes() {
        String[] source = {"a", "b", "c", "d"};
        String[] dest = new String[2];
        
        // This will throw ArrayIndexOutOfBoundsException
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            batchProcessor.copyArrays(source, dest);
        });
    }
    
    @Test
    @DisplayName("Should remove items from list")
    void testRemoveItems() {
        List<String> items = new ArrayList<>();
        items.add("test_item1");
        items.add("other_item");
        items.add("test_item2");
        
        // This will throw ConcurrentModificationException
        assertThrows(java.util.ConcurrentModificationException.class, () -> {
            batchProcessor.removeItems(items, "test_");
        });
    }
    
    @Test
    @DisplayName("Should get last item from array")
    void testGetLastItem() {
        String[] items = {"item1", "item2", "item3"};
        String result = batchProcessor.getLastItem(items);
        
        assertEquals("item3", result);
    }
    
    @Test
    @DisplayName("Should handle get last item from null array")
    void testGetLastItem_null() {
        String result = batchProcessor.getLastItem(null);
        
        assertNull(result);
    }
    
    @Test
    @DisplayName("Should handle get last item from empty array")
    void testGetLastItem_empty() {
        String[] items = {};
        String result = batchProcessor.getLastItem(items);
        
        assertNull(result);
    }
    
    @Test
    @DisplayName("Should process first item from list")
    void testProcessFirst() {
        List<String> items = new ArrayList<>();
        items.add("first");
        items.add("second");
        
        String result = batchProcessor.processFirst(items);
        
        assertEquals("first", result);
    }
    
    @Test
    @DisplayName("Should handle process first with null list")
    void testProcessFirst_null() {
        String result = batchProcessor.processFirst(null);
        
        assertNull(result);
    }
    
    @Test
    @DisplayName("Should handle process first with empty list")
    void testProcessFirst_empty() {
        List<String> items = new ArrayList<>();
        String result = batchProcessor.processFirst(items);
        
        assertNull(result);
    }
}
