package com.uow.fracategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
public class FRACategoryPage {

    @Autowired private CreateFRACategoryController createController;
    @Autowired private UpdateFRACategoryController updateController;
    @Autowired private ViewFRACategoryController viewController;
    @Autowired private SuspendFRACategoryController suspendController;
    @Autowired private SearchFRACategoryController searchController;

    @GetMapping("/api/fra-categories/list")
    @ResponseBody
    public Object onListCategory() {
        Map<String, Object> searchData = new HashMap<>();
        searchData.put("categoryName", "");
        searchData.put("categoryStatus", "");
        return searchController.searchCategory(searchData);
    }

    @PostMapping("/api/fra-categories/create")
    @ResponseBody
    public Object onCreateCategory(@RequestBody Map<String, Object> data) {
        try {
            String name = text(data.get("categoryName"));
            if (name.isBlank()) return errorMap("Category name is empty.");
            boolean result = createController.createCategory(data);
            return result ? true : errorMap("Database error.");
        } catch (IllegalArgumentException e) {
            return errorMap(e.getMessage());
        }
    }

    // Helper for JSON error response in JDK 8
    private Map<String, Object> errorMap(String msg) {
        Map<String, Object> err = new HashMap<>();
        err.put("error", msg);
        err.put("success", false);
        return err;
    }

    private String text(Object v) { return v == null ? "" : String.valueOf(v).trim(); }
}
