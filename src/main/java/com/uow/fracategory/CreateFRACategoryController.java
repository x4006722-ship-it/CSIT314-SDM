package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Controller
public class CreateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public boolean createCategory(Object newCategoryData) {
        if (!(newCategoryData instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Invalid data format.");
        }

        String newName = map.get("categoryName") == null ? "" : String.valueOf(map.get("categoryName")).trim();

        if (newName.isBlank()) {
            return false;
        }

        Map<String, Object> searchData = new HashMap<>();
        searchData.put("categoryName", newName);
        Object existingRows = fraCategory.getSearchCategory(searchData);
        if (existingRows instanceof List<?> rows) {
            for (Object row : rows) {
                if (row instanceof Map<?, ?> r) {
                    String found = r.get("categoryName") == null ? "" : String.valueOf(r.get("categoryName")).trim();
                    if (newName.equalsIgnoreCase(found)) {
                        throw new IllegalArgumentException("Category already exists.");
                    }
                }
            }
        }

        return fraCategory.saveCreateCategory(newCategoryData);
    }
}
