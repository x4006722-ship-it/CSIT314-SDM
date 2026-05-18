package com.uow.fracategory;

import org.springframework.stereotype.Controller;

/**
 * Retrieves FRA category information.
 * 
 * Responsibilities:
 * - Query category details by ID
 * - Return complete category information
 */
@Controller
public class ViewFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    /**
     * Retrieves a specific FRA category by its ID.
     * 
     * @param categoryID The category ID to retrieve
     * @return A Map with category details (categoryID, categoryName, categoryStatus), or null if not found
     */
    public Object viewCategory(int categoryID) {
        return fraCategory.getViewCategory(categoryID);
    }
}
