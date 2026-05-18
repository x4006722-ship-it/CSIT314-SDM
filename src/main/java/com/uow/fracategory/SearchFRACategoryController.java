package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class SearchFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();
    public Object searchCategory(Object searchCategoryData) {
        // 1. Parameter validation 
        if (searchCategoryData instanceof Map<?, ?> map) {
            String categoryStatus = readText(map.get("categoryStatus"));
            
            if (!categoryStatus.isBlank() && 
                !"Active".equalsIgnoreCase(categoryStatus) && 
                !"Suspended".equalsIgnoreCase(categoryStatus)) {
                // handle invalid status format 
                throw new IllegalArgumentException("Invalid status format.");
            }
        }
        
        // 2.parameter validation passed
        return fraCategory.getSearchCategory(searchCategoryData);
    }

    // Helper method to safely read text values 
    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
