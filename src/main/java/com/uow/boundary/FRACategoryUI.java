package com.uow.boundary;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uow.control.FRACategoryController;

@Controller
public class FRACategoryUI {

    @Autowired
    private FRACategoryController fraCategoryController;

    // Common methods for Category
    @GetMapping("/fra-category")
    public String showCategoryPage() {
        return "forward:/PlatformPage.html";
    }

    public String showCategorySuccessMessage() {
        String msg = fraCategoryController.successMessage == null ? "Success." : fraCategoryController.successMessage;
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        return "redirect:/PlatformPage.html?toast=" + encoded;
    }

    public String showCategoryErrorMessage() {
        String msg = fraCategoryController.errorMessage == null ? "Operation failed." : fraCategoryController.errorMessage;
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        return "redirect:/PlatformPage.html?toast=" + encoded;
    }
    //Common methods for Category
    
    // Create Category
    @PostMapping(path = "/api/fra-categories/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onCreateCategory(@RequestParam("categoryName") String categoryName,
                                   @RequestParam("categoryStatus") String categoryStatus) {
        boolean ok = fraCategoryController.createCategory(categoryName, categoryStatus);
        return ok ? showCategorySuccessMessage() : showCategoryErrorMessage();
    }

    // View Category
    @GetMapping("/api/fra-categories/view")
    @ResponseBody
    public Object onViewCategory(@RequestParam(value = "categoryID", defaultValue = "0") int categoryID) {
        return fraCategoryController.viewCategory(categoryID);
    }

    // Update Category
    @PostMapping(path = "/api/fra-categories/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onUpdateCategory(@RequestParam(value = "categoryID", defaultValue = "0") int categoryID,
                                   @RequestParam(value = "categoryName", defaultValue = "") String categoryName,
                                   @RequestParam(value = "categoryStatus", defaultValue = "") String categoryStatus) {
        boolean ok = fraCategoryController.updateCategory(categoryID, categoryName, categoryStatus);
        return ok
                ? java.util.Map.of("success", true)
                : java.util.Map.of("success", false, "error",
                        fraCategoryController.errorMessage == null ? "Update failed." : fraCategoryController.errorMessage);
    }

    // Suspend Category
    @PostMapping(path = "/api/fra-categories/suspend", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSuspendCategory(@RequestParam(value = "categoryID", defaultValue = "0") int categoryID) {
        boolean ok = fraCategoryController.suspendCategory(categoryID);
        return ok
                ? java.util.Map.of("success", true)
                : java.util.Map.of("success", false, "error",
                        fraCategoryController.errorMessage == null ? "Operation failed." : fraCategoryController.errorMessage);
    }

    // Search Category
    @GetMapping({"/api/fra-categories/search", "/api/fra-categories/list"})
    @ResponseBody
    public Object onSearchCategory(@RequestParam(value = "categoryName", defaultValue = "") String categoryName,
                                   @RequestParam(value = "categoryStatus", defaultValue = "") String categoryStatus) {
        Object result = fraCategoryController.searchCategory(categoryName, categoryStatus);
        return result == null ? java.util.List.of() : result;
    }
}
