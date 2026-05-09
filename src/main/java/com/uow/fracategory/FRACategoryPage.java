package com.uow.fracategory;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FRACategoryPage {

    private static final Pattern ID_PATTERN = Pattern.compile("^\\d+$");

    @Autowired
    private CreateFRACategoryController createFRACategoryController;

    @Autowired
    private ViewFRACategoryController viewFRACategoryController;

    @Autowired
    private UpdateFRACategoryController updateFRACategoryController;

    @Autowired
    private SuspendFRACategoryController suspendFRACategoryController;

    @Autowired
    private SearchFRACategoryController searchFRACategoryController;

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

    //Create Category
    @PostMapping("/api/fra-categories/create")
    @ResponseBody
    public boolean onCreateCategory(@RequestBody(required = false) Object newCategoryData,
                                    @RequestParam Map<String, String> formData) {
        if (!(newCategoryData instanceof Map<?, ?>) || ((Map<?, ?>) newCategoryData).isEmpty()) {
            newCategoryData = formData;
        }
        if (!(newCategoryData instanceof Map<?, ?> data)) {
            categoryMessage = "Type mismatch detected.";
            return false;
        }
        String categoryName = readText(data.get("categoryName"));
        String categoryStatus = readText(data.get("categoryStatus"));
        if (categoryName.isBlank() || categoryStatus.isBlank()) {
            categoryMessage = "Empty field detected.";
            return false;
        }
        if (!isValidStatus(categoryStatus)) {
            categoryMessage = "Invalid status format.";
            return false;
        }

        boolean result = createFRACategoryController.createCategory(newCategoryData);
        categoryMessage = result ? "Category created successfully." : "Category create failed.";
        return result;
    }

    //View Category
    @GetMapping("/api/fra-categories/view")
    @ResponseBody
    public Object onViewCategory(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                 @RequestParam(value = "categoryID", required = false) Integer categoryID) {
        int resolvedCategoryId = categoryId != null ? categoryId : (categoryID != null ? categoryID : 0);
        if (resolvedCategoryId <= 0) {
            return Map.of("error", "Empty field detected.");
        }
        return viewFRACategoryController.viewCategory(resolvedCategoryId);
    }

    //Update Category
    @PostMapping("/api/fra-categories/update")
    @ResponseBody
    public boolean onUpdateCategory(@RequestBody(required = false) Object updatedCategoryData,
                                    @RequestParam Map<String, String> queryData) {
        if (!(updatedCategoryData instanceof Map<?, ?>) || ((Map<?, ?>) updatedCategoryData).isEmpty()) {
            java.util.Map<String, Object> normalized = new java.util.HashMap<>();
            normalized.put("categoryId", readText(queryData.get("categoryId")));
            if (readText(normalized.get("categoryId")).isBlank()) {
                normalized.put("categoryId", readText(queryData.get("categoryID")));
            }
            normalized.put("categoryName", readText(queryData.get("categoryName")));
            normalized.put("categoryStatus", readText(queryData.get("categoryStatus")));
            updatedCategoryData = normalized;
        }
        if (!(updatedCategoryData instanceof Map<?, ?> data)) {
            categoryMessage = "Type mismatch detected.";
            return false;
        }
        String categoryIdText = readText(data.get("categoryId"));
        if (categoryIdText.isBlank() || !ID_PATTERN.matcher(categoryIdText).matches()) {
            categoryMessage = "Type mismatch detected.";
            return false;
        }
        String categoryStatus = readText(data.get("categoryStatus"));
        if (!categoryStatus.isBlank() && !isValidStatus(categoryStatus)) {
            categoryMessage = "Invalid status format.";
            return false;
        }

        boolean result = updateFRACategoryController.updateCategory(updatedCategoryData);
        categoryMessage = result ? "Category updated successfully." : "Category update failed.";
        return result;
    }

    //Suspend Category
    @PostMapping("/api/fra-categories/suspend")
    @ResponseBody
    public boolean onSuspendCategory(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                     @RequestParam(value = "categoryID", required = false) Integer categoryID) {
        int resolvedCategoryId = categoryId != null ? categoryId : (categoryID != null ? categoryID : 0);
        if (resolvedCategoryId <= 0) {
            categoryMessage = "Empty field detected.";
            return false;
        }
        boolean result = suspendFRACategoryController.suspendCategory(resolvedCategoryId);
        categoryMessage = result ? "Category suspended successfully." : "Category suspend failed.";
        return result;
    }

    //Search Category
    @GetMapping("/api/fra-categories/list")
    @ResponseBody
    public Object onListCategory() {
        java.util.Map<String, Object> searchData = new java.util.HashMap<>();
        searchData.put("categoryName", "");
        searchData.put("categoryStatus", "");
        return searchFRACategoryController.searchCategory(searchData);
    }

    @GetMapping("/api/fra-categories/search")
    @ResponseBody
    public Object onSearchCategoryGet(@RequestParam Map<String, String> queryData) {
        java.util.Map<String, Object> searchData = new java.util.HashMap<>();
        searchData.put("categoryName", readText(queryData.get("categoryName")));
        searchData.put("categoryStatus", readText(queryData.get("categoryStatus")));

        String categoryStatus = readText(searchData.get("categoryStatus"));
        if (!categoryStatus.isBlank() && !isValidStatus(categoryStatus)) {
            return Map.of("error", "Invalid status format.");
        }
        return searchFRACategoryController.searchCategory(searchData);
    }

    @PostMapping({"/api/fra-categories/search", "/api/fra-categories/list"})
    @ResponseBody
    public Object onSearchCategory(@RequestBody Object searchCategoryData) {
        if (!(searchCategoryData instanceof Map<?, ?> data)) {
            return Map.of("error", "Type mismatch detected.");
        }
        String categoryStatus = readText(data.get("categoryStatus"));
        if (!categoryStatus.isBlank() && !isValidStatus(categoryStatus)) {
            return Map.of("error", "Invalid status format.");
        }
        return searchFRACategoryController.searchCategory(searchCategoryData);
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private boolean isValidStatus(String status) {
        return "Active".equalsIgnoreCase(status) || "Suspended".equalsIgnoreCase(status);
    }
}
