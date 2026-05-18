package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.*;

@Controller
public class CreateFRACategoryController {
    private final FRACategory fraCategory = new FRACategory();

    public boolean createCategory(Map<String, Object> cleanData) {
        String newName = String.valueOf(cleanData.get("categoryName")).trim();

        // Business Logic: Prevent duplicate names in the system
        List<?> existing = (List<?>) fraCategory.getSearchCategory(Map.of("categoryName", newName));
        for (Object obj : existing) {
            Map<?, ?> row = (Map<?, ?>) obj;
            if (newName.equalsIgnoreCase(String.valueOf(row.get("categoryName")))) {
                throw new IllegalArgumentException("Category already exists.");
            }
        }

        return fraCategory.saveCreateCategory(cleanData);
    }
}