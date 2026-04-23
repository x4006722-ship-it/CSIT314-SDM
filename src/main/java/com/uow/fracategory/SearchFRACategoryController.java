package com.uow.fracategory;

import org.springframework.stereotype.Controller;

@Controller
public class SearchFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public Object searchCategory(String keyword, String status) {
        return fraCategory.getSearchCategory(keyword, status);
    }
}
