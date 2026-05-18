package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;

/**
 * Handles searching for FRA categories with validation.
 * 
 * Responsibilities:
 * - Validate search/filter criteria
 * - Enforce valid status values in searches
 * - Delegate to FRACategory entity for database query
 * - Return list of matching categories
 */
@Controller
public class SearchFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    /**
     * Searches for FRA categories with optional filter criteria.
     * 
     * Validates:
     * - If status filter is provided, it must be valid (Active, Suspended, or blank/all)
     * 
     * @param searchCategoryData A Map with optional filters: categoryName, categoryStatus
     * @return List of Maps representing matching categories
     * @throws IllegalArgumentException if status format is invalid
     */
    public Object searchCategory(Object searchCategoryData) {
        // 1. 在业务层集中进行状态格式的校验
        if (searchCategoryData instanceof Map<?, ?> map) {
            String categoryStatus = readText(map.get("categoryStatus"));
            
            if (!categoryStatus.isBlank() && 
                !"Active".equalsIgnoreCase(categoryStatus) && 
                !"Suspended".equalsIgnoreCase(categoryStatus)) {
                // 抛出异常，统一处理！
                throw new IllegalArgumentException("Invalid status format.");
            }
        }
        
        // 2. 校验通过，交给底层 DAO
        return fraCategory.getSearchCategory(searchCategoryData);
    }

    // 辅助方法
    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
