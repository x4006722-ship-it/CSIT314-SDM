package com.uow.fracategory;

import org.springframework.stereotype.Controller;

@Controller
public class SuspendFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    private String errorMessage = "";

    public String getErrorMessage() { return errorMessage; }

    public boolean suspendCategory(int categoryID) {
        errorMessage = "";

        if (categoryID <= 0) { errorMessage = "Invalid category."; return false; }

        if (!fraCategory.saveSuspendCategory(categoryID)) {
            errorMessage = fraCategory.lastErrorMessage;
            return false;
        }
        return true;
    }
}
