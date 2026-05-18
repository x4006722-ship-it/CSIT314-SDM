package com.uow.fracategory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FRACategoryPage {

    @Autowired private CreateFRACategoryController createFRACategoryController;
    @Autowired private ViewFRACategoryController viewFRACategoryController;
    @Autowired private UpdateFRACategoryController updateFRACategoryController;
    @Autowired private SuspendFRACategoryController suspendFRACategoryController;
    @Autowired private SearchFRACategoryController searchFRACategoryController;

    private String categoryMessage = "";

    @GetMapping("/fra-category")
    public String showCategoryPage() {
        return "forward:/PlatformPage.html";
    }

    public String showCategorySuccessMessage() {
        return categoryMessage.isBlank() ? "Category operation success." : categoryMessage;
    }

    public String showCategoryErrorMessage() {
        return categoryMessage.isBlank() ? "Category operation failed." : categoryMessage;
    }

    @PostMapping("/api/fra-categories/create")
    @ResponseBody
    public Object onCreateCategory(@RequestBody(required = false) Object newCategoryData,
                                   @RequestParam Map<String, String> formData) {
        if (!(newCategoryData instanceof Map<?, ?>) || ((Map<?, ?>) newCategoryData).isEmpty()) {
            newCategoryData = formData;
        }
        if (!(newCategoryData instanceof Map<?, ?> data)) {
            return Map.of("error", "Type mismatch detected.");
        }
        String categoryName   = readText(data.get("categoryName"));
        String categoryStatus = readText(data.get("categoryStatus"));
        if (categoryName.isBlank() || categoryStatus.isBlank()) {
            return Map.of("error", "Empty field detected.");
        }
        if (!isValidStatus(categoryStatus)) {
            return Map.of("error", "Invalid status format.");
        }
        try {
            return createFRACategoryController.createCategory(newCategoryData) ? true : Map.of("error", "Database save failed.");
        } catch (IllegalArgumentException e) {
            return Map.of("error", e.getMessage());
        }
    }

    @GetMapping("/api/fra-categories/view")
    @ResponseBody
    public Object onViewCategory(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                 @RequestParam(value = "categoryID", required = false) Integer categoryID) {
        int resolved = categoryId != null ? categoryId : (categoryID != null ? categoryID : 0);
        if (resolved <= 0) {
            return Map.of("error", "Empty field detected.");
        }
        return viewFRACategoryController.viewCategory(resolved);
    }

    @PostMapping("/api/fra-categories/update")
    @ResponseBody
    public Object onUpdateCategory(@RequestBody(required = false) Object updatedCategoryData,
                                   @RequestParam Map<String, String> queryData) {
        if (!(updatedCategoryData instanceof Map<?, ?>) || ((Map<?, ?>) updatedCategoryData).isEmpty()) {
            Map<String, Object> normalized = new HashMap<>();
            String id = readText(queryData.get("categoryId"));
            normalized.put("categoryId", id.isBlank() ? readText(queryData.get("categoryID")) : id);
            normalized.put("categoryName", readText(queryData.get("categoryName")));
            normalized.put("categoryStatus", readText(queryData.get("categoryStatus")));
            updatedCategoryData = normalized;
        }
        if (!(updatedCategoryData instanceof Map<?, ?> data)) {
            return Map.of("error", "Type mismatch detected.");
        }
        String categoryStatus = readText(data.get("categoryStatus"));
        if (!categoryStatus.isBlank() && !isValidStatus(categoryStatus)) {
            return Map.of("error", "Invalid status format.");
        }
        try {
            return updateFRACategoryController.updateCategory(updatedCategoryData) ? true : Map.of("error", "Database update failed.");
        } catch (IllegalArgumentException e) {
            return Map.of("error", e.getMessage());
        }
    }

    @PostMapping("/api/fra-categories/suspend")
    @ResponseBody
    public boolean onSuspendCategory(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                     @RequestParam(value = "categoryID", required = false) Integer categoryID) {
        int resolved = categoryId != null ? categoryId : (categoryID != null ? categoryID : 0);
        if (resolved <= 0) {
            categoryMessage = "Empty field detected.";
            return false;
        }
        boolean result = suspendFRACategoryController.suspendCategory(resolved);
        categoryMessage = result ? "Category suspended successfully." : "Category suspend failed.";
        return result;
    }

    @GetMapping({"/api/fra-categories/search", "/api/fra-categories/list"})
    @ResponseBody
    public Object onSearchCategory(@RequestParam(required = false) Map<String, String> queryData) {
        Map<String, Object> searchData = new HashMap<>();
        searchData.put("categoryName",   readText(queryData != null ? queryData.get("categoryName")   : null));
        searchData.put("categoryStatus", readText(queryData != null ? queryData.get("categoryStatus") : null));

        String status = readText(searchData.get("categoryStatus"));
        if (!status.isBlank() && !isValidStatus(status) && !"all".equalsIgnoreCase(status)) {
            return Map.of("error", "Invalid status format.");
        }
        return searchFRACategoryController.searchCategory(searchData);
    }

    private boolean isValidStatus(String status) {
        return "Active".equalsIgnoreCase(status) || "Suspended".equalsIgnoreCase(status);
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
