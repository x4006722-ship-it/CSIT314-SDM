package com.uow.fracategory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FRACategoryPage {

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

    @GetMapping("/fra-category")
    public String showCategoryPage() {
        return "forward:/PlatformPage.html";
    }

    public String showCategorySuccessMessage(String message) {
        String msg = message == null ? "" : message;
        return "redirect:/PlatformPage.html?toast=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
    }

    public String showCategoryErrorMessage(String message) {
        String msg = message == null || message.isBlank() ? "" : message;
        return "redirect:/PlatformPage.html?toast=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
    }

    // Create Category
    @PostMapping(path = "/api/fra-categories/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onCreateCategory(@RequestParam(value = "categoryName", defaultValue = "") String categoryName,
                                   @RequestParam(value = "categoryStatus", defaultValue = "") String categoryStatus) {
        boolean ok = createFRACategoryController.createCategory(categoryName, categoryStatus);
        return ok
                ? showCategorySuccessMessage("Category created successfully.")
                : showCategoryErrorMessage(createFRACategoryController.getErrorMessage());
    }

    // View Category
    @GetMapping("/api/fra-categories/view")
    @ResponseBody
    public Object onViewCategory(@RequestParam(value = "categoryID", defaultValue = "0") int categoryID) {
        return viewFRACategoryController.viewCategory(categoryID);
    }

    // Update Category
    @PostMapping(path = "/api/fra-categories/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onUpdateCategory(@RequestParam(value = "categoryID", defaultValue = "0") int categoryID,
                                   @RequestParam(value = "categoryName", defaultValue = "") String categoryName,
                                   @RequestParam(value = "categoryStatus", defaultValue = "") String categoryStatus) {
        boolean ok = updateFRACategoryController.updateCategory(categoryID, categoryName, categoryStatus);
        return ok
                ? java.util.Map.of("success", true, "message", "Category updated successfully.")
                : java.util.Map.of("success", false, "error", updateFRACategoryController.getErrorMessage());
    }

    // Suspend Category
    @PostMapping(path = "/api/fra-categories/suspend", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSuspendCategory(@RequestParam(value = "categoryID", defaultValue = "0") int categoryID) {
        boolean ok = suspendFRACategoryController.suspendCategory(categoryID);
        return ok
                ? java.util.Map.of("success", true, "message", "Category status updated successfully.")
                : java.util.Map.of("success", false, "error", suspendFRACategoryController.getErrorMessage());
    }

    // Search Category
    @GetMapping({"/api/fra-categories/search", "/api/fra-categories/list"})
    @ResponseBody
    public Object onSearchCategory(@RequestParam(value = "categoryName", defaultValue = "") String categoryName,
                                   @RequestParam(value = "categoryStatus", defaultValue = "") String categoryStatus) {
        Object result = searchFRACategoryController.searchCategory(categoryName, categoryStatus);
        return result == null ? java.util.List.of() : result;
    }
}
