package com.uow.fracategory;

import org.springframework.stereotype.Controller;

@Controller
public class ViewFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public Object viewCategory(int categoryID) {
        return fraCategory.getViewCategory(categoryID);
    }
}
