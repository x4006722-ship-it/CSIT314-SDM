package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class SearchFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public Object searchCategory(Object searchCategoryData) {
        // 1. Validate status format in the business layer
        if (searchCategoryData instanceof Map<?, ?> map) {
            String categoryStatus = readText(map.get("categoryStatus"));
            
            if (!categoryStatus.isBlank() && 
                !"Active".equalsIgnoreCase(categoryStatus) && 
                !"Suspended".equalsIgnoreCase(categoryStatus)) {
                // Throw exception for unified error handling
                throw new IllegalArgumentException("Invalid status format.");
            }
        }
        
        // 2. Validation passed — delegate to DAO
        return fraCategory.getSearchCategory(searchCategoryData);
    }

    // Helper method
    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
