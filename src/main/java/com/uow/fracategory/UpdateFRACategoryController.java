package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Handles updates to FRA categories with comprehensive validation.
 * 
 * Responsibilities:
 * - Validate category ID and data
 * - Enforce valid status values
 * - Check for duplicate names (excluding current category)
 * - Persist changes to database
 * - Throw exceptions for validation failures
 */
@Controller
public class UpdateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    // 保持返回 boolean 不变！
    /**
     * Updates an existing FRA category with validation.
     * 
     * Validates:
     * - Category ID is valid
     * - Category exists
     * - New name and status are not empty
     * - Status is valid (Active or Suspended)
     * - Name is not a duplicate (excluding this category)
     * 
     * @param updatedCategoryData A Map with categoryId, categoryName, categoryStatus
     * @return true if update was successful
     * @throws IllegalArgumentException if validation fails
     */
    public boolean updateCategory(Object updatedCategoryData) {
        if (!(updatedCategoryData instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Invalid data format.");
        }

        int categoryId = parseInt(map.get("categoryId"));
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }

        Object currentCategory = fraCategory.getViewCategory(categoryId);
        if (!(currentCategory instanceof Map<?, ?> currentMap)) {
            throw new IllegalArgumentException("Category not found.");
        }

        String incomingName = readText(map.get("categoryName"));
        String incomingStatus = readText(map.get("categoryStatus"));

        // ==========================================
        // 1. 精准边界检查：不再静默使用旧值，直接报错拦截！
        // ==========================================
        if (incomingName.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        if (incomingStatus.isBlank()) {
            throw new IllegalArgumentException("Category status cannot be empty.");
        }
        if (!"Active".equalsIgnoreCase(incomingStatus) && !"Suspended".equalsIgnoreCase(incomingStatus)) {
            throw new IllegalArgumentException("Invalid status format.");
        }

        // ==========================================
        // 2. 查重逻辑（排除自身）
        // ==========================================
        Map<String, Object> searchData = new HashMap<>();
        searchData.put("categoryName", incomingName);
        Object duplicatedRows = fraCategory.getSearchCategory(searchData);
        if (duplicatedRows instanceof List<?> rows) {
            for (Object row : rows) {
                if (row instanceof Map<?, ?> each) {
                    int foundId = parseInt(each.get("categoryID"));
                    String foundName = readText(each.get("categoryName"));
                    
                    // 如果发现同名，且那个同名的 ID 不是我们正在修改的这条数据的 ID
                    if (foundId != categoryId && incomingName.equalsIgnoreCase(foundName)) {
                        throw new IllegalArgumentException("Category already exists.");
                    }
                }
            }
        }

        // ==========================================
        // 3. 一切合规，执行更新
        // ==========================================
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", categoryId);
        updateData.put("categoryName", incomingName);
        updateData.put("categoryStatus", incomingStatus);
        
        return fraCategory.saveUpdateCategory(updateData);
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int parseInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(readText(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}