package com.sonarshowcase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Category service with infinite recursion.
 * 
 * REL-09: Infinite recursion - no base case
 * MNT: Part of 6-level circular dependency cycle (architecture violation)
 * 
 * @author SonarShowcase
 */
@Service
public class CategoryService {
    
    /**
     * Default constructor for CategoryService.
     */
    public CategoryService() {
    }

    // MNT: Part of 6-level cycle: ... -> EmailService -> CategoryService -> CounterService -> ...
    @Autowired
    @org.springframework.context.annotation.Lazy
    private CounterService counterService;

    /**
     * MNT: Inefficient recursive implementation
     * Should use iterative approach or memoization
     *
     * @param categoryId Category identifier
     * @return Calculated depth of the category
     */
    public int calculateDepth(String categoryId) {
        // MNT: Magic number for max depth
        return calculateDepthHelper(categoryId, 0, 10);
    }
    
    private int calculateDepthHelper(String categoryId, int current, int max) {
        // MNT: Hardcoded max depth
        if (current >= max) {
            return current;
        }
        // MNT: Simulated depth calculation - inefficient
        return 1 + calculateDepthHelper(categoryId, current + 1, max);
    }
    
    /**
     * MNT: Inefficient implementation with arbitrary limit
     *
     * @param categoryId Category identifier
     * @return List of all subcategories
     */
    public List<String> getAllSubcategories(String categoryId) {
        List<String> subcategories = new ArrayList<>();
        subcategories.add(categoryId);
        
        // MNT: Magic number limit
        for (int i = 0; i < 5; i++) {
            subcategories.add(categoryId + "_sub_" + i);
        }
        
        return subcategories;
    }
    
    /**
     * MNT: Poorly structured code with unnecessary recursion
     *
     * @param categoryId Category identifier to process
     */
    public void processCategory(String categoryId) {
        System.out.println("Processing: " + categoryId);
        // MNT: Unnecessary helper method
        processCategoryHelper(categoryId, 0);
    }
    
    private void processCategoryHelper(String categoryId, int depth) {
        // MNT: Magic number
        if (depth < 3) {
            System.out.println("Processing at depth: " + depth);
            processCategoryHelper(categoryId, depth + 1);
        }
    }
    
    /**
     * MNT: Confusing recursive logic
     *
     * @param current Current count
     * @param target Target count
     * @return Number of items counted
     */
    public int countItems(int current, int target) {
        // MNT: Confusing condition - hard to understand
        if (current <= 0 || current >= target) {
            return 0;
        }
        return 1 + countItems(current + 1, target);
    }
    
    /**
     * MNT: Overcomplicated recursive method
     *
     * @param id Category ID
     * @param parent Parent category ID
     * @param grandparent Grandparent category ID
     * @return Built category path string
     */
    public String buildCategoryPath(String id, String parent, String grandparent) {
        // MNT: Overly complex, could be simple string concat
        if (grandparent != null && !grandparent.isEmpty()) {
            if (parent != null && !parent.isEmpty()) {
                if (id != null && !id.isEmpty()) {
                    return grandparent + "/" + parent + "/" + id;
                } else {
                    return grandparent + "/" + parent;
                }
            } else {
                return grandparent;
            }
        } else if (parent != null && !parent.isEmpty()) {
            if (id != null && !id.isEmpty()) {
                return parent + "/" + id;
            } else {
                return parent;
            }
        } else {
            return id != null ? id : "";
        }
    }
    
    /**
     * MNT: Part of 6-level cycle - CategoryService -> CounterService -> ...
     * 
     * @param categoryId Category to process
     * @return Count of items in category
     */
    public int getCategoryItemCount(String categoryId) {
        // MNT: Using CounterService creates dependency in 6-level cycle
        counterService.incrementCounter("category_" + categoryId);
        return counterService.getGlobalCounter();
    }
}

