package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Controller
public class CreateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    // Returns boolean
    public boolean createCategory(Object newCategoryData) {
        if (!(newCategoryData instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Invalid data format.");
        }

        String newName = readText(map.get("categoryName"));
        String newStatus = readText(map.get("categoryStatus"));

        // 1. Boundary check — reject missing name
        if (newName.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        
        // Reject missing status
        if (newStatus.isBlank()) {
            throw new IllegalArgumentException("Category status is missing.");
        }
        
        // Reject invalid status values
        if (!"Active".equalsIgnoreCase(newStatus) && !"Suspended".equalsIgnoreCase(newStatus)) {
            throw new IllegalArgumentException("Invalid status format.");
        }

        // 2. Business logic: duplicate check
        Map<String, Object> searchData = new HashMap<>();
        searchData.put("categoryName", newName);
        Object existingRows = fraCategory.getSearchCategory(searchData);

        if (existingRows instanceof List<?> rows) {
            for (Object row : rows) {
                if (row instanceof Map<?, ?> r) {
                    String categoryName = readText(r.get("categoryName"));
                    if (newName.equalsIgnoreCase(categoryName)) {
                        // Throw exception to interrupt flow
                        throw new IllegalArgumentException("Category already exists.");
                    }
                }
            }
        }

        // 3. All checks passed — persist
        return fraCategory.saveCreateCategory(newCategoryData);
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}