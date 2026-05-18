package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Controller
public class UpdateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public boolean updateCategory(Object updatedCategoryData) {
        if (!(updatedCategoryData instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Invalid data format.");
        }

        Object rawId = map.get("categoryId");
        int categoryId = rawId instanceof Number n ? n.intValue() : 0;
        if (categoryId <= 0) {
            try { categoryId = Integer.parseInt(String.valueOf(rawId).trim()); } catch (NumberFormatException ignored) {}
        }
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }

        if (fraCategory.getViewCategory(categoryId) == null) {
            throw new IllegalArgumentException("Category not found.");
        }

        String incomingName = map.get("categoryName") == null ? "" : String.valueOf(map.get("categoryName")).trim();

        if (!incomingName.isBlank()) {
            Map<String, Object> searchData = new HashMap<>();
            searchData.put("categoryName", incomingName);
            Object duplicatedRows = fraCategory.getSearchCategory(searchData);
            if (duplicatedRows instanceof List<?> rows) {
                for (Object row : rows) {
                    if (row instanceof Map<?, ?> each) {
                        Object foundIdRaw = each.get("categoryID");
                        int foundId = foundIdRaw instanceof Number n ? n.intValue() : 0;
                        String foundName = each.get("categoryName") == null ? "" : String.valueOf(each.get("categoryName")).trim();
                        if (foundId != categoryId && incomingName.equalsIgnoreCase(foundName)) {
                            throw new IllegalArgumentException("Category already exists.");
                        }
                    }
                }
            }
        }

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", categoryId);
        updateData.put("categoryName", incomingName);
        updateData.put("categoryStatus", map.get("categoryStatus") == null ? "" : String.valueOf(map.get("categoryStatus")).trim());
        return fraCategory.saveUpdateCategory(updateData);
    }
}
