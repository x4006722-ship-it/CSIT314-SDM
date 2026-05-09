package com.uow.fracategory;

import org.springframework.stereotype.Controller;

@Controller
public class CreateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public boolean createCategory(Object newCategoryData) {
        if (!(newCategoryData instanceof java.util.Map<?, ?> map)) {
            return false;
        }

        String newName = readText(map.get("categoryName"));
        java.util.Map<String, Object> searchData = new java.util.HashMap<>();
        searchData.put("categoryName", newName);
        Object existingRows = fraCategory.getSearchCategory(searchData);

        if (existingRows instanceof java.util.List<?> rows) {
            for (Object row : rows) {
                if (row instanceof java.util.Map<?, ?> r) {
                    String categoryName = readText(r.get("categoryName"));
                    if (newName.equalsIgnoreCase(categoryName)) {
                        return false;
                    }
                }
            }
        }

        return fraCategory.saveCreateCategory(newCategoryData);
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
