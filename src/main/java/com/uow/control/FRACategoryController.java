package com.uow.control;

import org.springframework.stereotype.Service;

import com.uow.entity.FRACategory;

@Service
public class FRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public String errorMessage = "";
    public String successMessage = "";

    private void clearFeedback() {
        errorMessage = "";
        successMessage = "";
    }

    // Create Category
    public boolean createCategory(String categoryName, String categoryStatus) {
        clearFeedback();
        if (categoryName == null || categoryName.isBlank()) {
            errorMessage = "Category name is required.";
            return false;
        }
        if (categoryStatus == null || categoryStatus.isBlank()) {
            errorMessage = "Category status is required.";
            return false;
        }
        String st = categoryStatus.trim();
        if (!("Active".equalsIgnoreCase(st) || "Suspended".equalsIgnoreCase(st))) {
            errorMessage = "Invalid category status.";
            return false;
        }

        fraCategory.categoryName = categoryName;
        fraCategory.categoryStatus = st;
        boolean ok = fraCategory.saveCreateCategory();
        if (!ok) {
            errorMessage = fraCategory.lastErrorMessage == null || fraCategory.lastErrorMessage.isBlank()
                    ? "Failed to create category."
                    : fraCategory.lastErrorMessage;
            return false;
        }
        successMessage = "Category created successfully.";
        return true;
    }

    // View Category
    public Object viewCategory(int categoryID) {
        clearFeedback();
        Object result = fraCategory.getViewCategory(categoryID);
        if (result == null) {
            errorMessage = fraCategory.lastErrorMessage != null && !fraCategory.lastErrorMessage.isBlank()
                    ? fraCategory.lastErrorMessage
                    : "Category not found.";
            return null;
        }
        return result;
    }

    // Update Category
    public boolean updateCategory(int categoryID, String categoryName, String categoryStatus) {
        clearFeedback();
        if (categoryID <= 0) {
            errorMessage = "Invalid category.";
            return false;
        }

        String namePart = categoryName == null ? "" : categoryName;
        String statusPart = categoryStatus == null ? "" : categoryStatus;

        if (!statusPart.isBlank()) {
            String st = statusPart.trim();
            if (!("Active".equalsIgnoreCase(st) || "Suspended".equalsIgnoreCase(st))) {
                errorMessage = "Invalid category status.";
                return false;
            }
        }
        if (!namePart.isBlank() && namePart.length() > 500) {
            errorMessage = "Category name is too long.";
            return false;
        }

        fraCategory.categoryName = namePart;
        fraCategory.categoryStatus = statusPart;
        boolean ok = fraCategory.saveUpdateCategory(categoryID);
        if (!ok) {
            errorMessage = fraCategory.lastErrorMessage == null || fraCategory.lastErrorMessage.isBlank()
                    ? "Failed to update category."
                    : fraCategory.lastErrorMessage;
            return false;
        }
        successMessage = "Category updated successfully.";
        return true;
    }

    // Suspend Category
    public boolean suspendCategory(int categoryID) {
        clearFeedback();
        if (categoryID <= 0) {
            errorMessage = "Invalid category.";
            return false;
        }
        boolean ok = fraCategory.saveSuspendCategory(categoryID);
        if (!ok) {
            errorMessage = fraCategory.lastErrorMessage == null || fraCategory.lastErrorMessage.isBlank()
                    ? "Failed to update category status."
                    : fraCategory.lastErrorMessage;
            return false;
        }
        successMessage = "Category status updated successfully.";
        return true;
    }

    // Search Category
    public Object searchCategory(String keyword, String status) {
        clearFeedback();
        try {
            return fraCategory.getSearchCategory(keyword, status);
        } catch (Exception e) {
            errorMessage = "Failed to search categories.";
            return java.util.List.of();
        }
    }
}
