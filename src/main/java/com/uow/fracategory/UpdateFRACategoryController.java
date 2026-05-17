package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Controller
public class UpdateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    // Returns boolean
    public boolean updateCategory(Object updatedCategoryData) {
        if (!(updatedCategoryData instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Invalid data format.");
        }

        int categoryId = parseInt(map.get("categoryId"));
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }

        if (fraCategory.getViewCategory(categoryId) == null) {
            throw new IllegalArgumentException("Category not found.");
        }

        String incomingName = readText(map.get("categoryName"));
        String incomingStatus = readText(map.get("categoryStatus"));

        // ==========================================
        // 1. Strict boundary check — reject missing or blank fields immediately
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
        // 2. Duplicate check (excluding current record)
        // ==========================================
        Map<String, Object> searchData = new HashMap<>();
        searchData.put("categoryName", incomingName);
        Object duplicatedRows = fraCategory.getSearchCategory(searchData);
        if (duplicatedRows instanceof List<?> rows) {
            for (Object row : rows) {
                if (row instanceof Map<?, ?> each) {
                    int foundId = parseInt(each.get("categoryID"));
                    String foundName = readText(each.get("categoryName"));
                    
                    // Found a duplicate name belonging to a different category
                    if (foundId != categoryId && incomingName.equalsIgnoreCase(foundName)) {
                        throw new IllegalArgumentException("Category already exists.");
                    }
                }
            }
        }

        // ==========================================
        // 3. All checks passed — execute update
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