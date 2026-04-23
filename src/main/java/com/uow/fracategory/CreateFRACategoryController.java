package com.uow.fracategory;

import org.springframework.stereotype.Controller;

@Controller
public class CreateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    private String errorMessage = "";

    public String getErrorMessage() { return errorMessage; }

    public boolean createCategory(String categoryName, String categoryStatus) {
        errorMessage = "";

        if (categoryName == null || categoryName.isBlank()) {
            errorMessage = "Category name is required.";
            return false;
        }
        if (categoryStatus == null || categoryStatus.isBlank()) {
            errorMessage = "Category status is required.";
            return false;
        }
        String st = categoryStatus.trim();
        if (!st.equalsIgnoreCase("Active") && !st.equalsIgnoreCase("Suspended")) {
            errorMessage = "Invalid category status.";
            return false;
        }

        fraCategory.categoryName = categoryName.trim();
        fraCategory.categoryStatus = st;

        if (!fraCategory.saveCreateCategory()) {
            errorMessage = fraCategory.lastErrorMessage;
            return false;
        }
        return true;
    }
}
