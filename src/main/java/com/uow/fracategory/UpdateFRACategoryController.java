package com.uow.fracategory;

import org.springframework.stereotype.Controller;

@Controller
public class UpdateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    private String errorMessage = "";

    public String getErrorMessage() { return errorMessage; }

    public boolean updateCategory(int categoryID, String categoryName, String categoryStatus) {
        errorMessage = "";

        if (categoryID <= 0) { errorMessage = "Invalid category."; return false; }

        String incomingName   = categoryName == null ? "" : categoryName.trim();
        String incomingStatus = categoryStatus == null ? "" : categoryStatus.trim();

        if (!incomingStatus.isEmpty()
                && !incomingStatus.equalsIgnoreCase("Active")
                && !incomingStatus.equalsIgnoreCase("Suspended")) {
            errorMessage = "Invalid category status.";
            return false;
        }
        if (!incomingName.isEmpty() && incomingName.length() > 500) {
            errorMessage = "Category name is too long.";
            return false;
        }

        // Merge with existing DB values so partial updates are supported
        Object existing = fraCategory.getViewCategory(categoryID);
        if (existing == null) { errorMessage = "Category not found."; return false; }

        String currentName   = "";
        String currentStatus = "";
        if (existing instanceof java.util.Map<?, ?> m) {
            Object n = m.get("categoryName");
            Object s = m.get("categoryStatus");
            currentName   = n == null ? "" : String.valueOf(n);
            currentStatus = s == null ? "" : String.valueOf(s);
        }

        fraCategory.categoryName   = incomingName.isEmpty()   ? currentName   : incomingName;
        fraCategory.categoryStatus = incomingStatus.isEmpty() ? currentStatus : incomingStatus;

        if (!fraCategory.saveUpdateCategory(categoryID)) {
            errorMessage = fraCategory.lastErrorMessage;
            return false;
        }
        return true;
    }
}
