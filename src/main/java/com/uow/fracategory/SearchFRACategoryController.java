package com.uow.fracategory;

import org.springframework.stereotype.Controller;

@Controller
public class SearchFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public Object searchCategory(Object searchCategoryData) {
        return fraCategory.getSearchCategory(searchCategoryData);
    }
}
