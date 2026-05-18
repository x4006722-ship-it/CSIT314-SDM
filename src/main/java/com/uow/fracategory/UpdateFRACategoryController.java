package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.*;

@Controller
public class UpdateFRACategoryController {
    private final FRACategory fraCategory = new FRACategory();

    public boolean updateCategory(Map<String, Object> cleanData) {
        int id = Integer.parseInt(String.valueOf(cleanData.get("categoryId")));
        String newName = String.valueOf(cleanData.get("categoryName")).trim();

        // Business Logic 1: Ensure category actually exists before updating
        if (fraCategory.getViewCategory(id) == null) {
            throw new IllegalArgumentException("Category not found.");
        }

        // Business Logic 2: Prevent duplicates (ignore if it's the same record's own name)
        List<?> existing = (List<?>) fraCategory.getSearchCategory(Map.of("categoryName", newName));
        for (Object obj : existing) {
            Map<?, ?> row = (Map<?, ?>) obj;
            int foundId = Integer.parseInt(String.valueOf(row.get("categoryID")));
            if (foundId != id && newName.equalsIgnoreCase(String.valueOf(row.get("categoryName")))) {
                throw new IllegalArgumentException("Category already exists.");
            }
        }

        return fraCategory.saveUpdateCategory(cleanData);
    }
}