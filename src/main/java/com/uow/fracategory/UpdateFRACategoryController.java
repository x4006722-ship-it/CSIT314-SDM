package com.uow.fracategory;

import org.springframework.stereotype.Controller;

@Controller
public class UpdateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public boolean updateCategory(Object updatedCategoryData) {
        if (!(updatedCategoryData instanceof java.util.Map<?, ?> map)) {
            return false;
        }

        int categoryId = parseInt(map.get("categoryId"));
        if (categoryId <= 0) {
            return false;
        }

        Object currentCategory = fraCategory.getViewCategory(categoryId);
        if (!(currentCategory instanceof java.util.Map<?, ?> currentMap)) {
            return false;
        }

        String incomingName = readText(map.get("categoryName"));
        String incomingStatus = readText(map.get("categoryStatus"));
        String finalName = incomingName.isBlank() ? readText(currentMap.get("categoryName")) : incomingName;
        String finalStatus = incomingStatus.isBlank() ? readText(currentMap.get("categoryStatus")) : incomingStatus;

        java.util.Map<String, Object> searchData = new java.util.HashMap<>();
        searchData.put("categoryName", finalName);
        Object duplicatedRows = fraCategory.getSearchCategory(searchData);
        if (duplicatedRows instanceof java.util.List<?> rows) {
            for (Object row : rows) {
                if (row instanceof java.util.Map<?, ?> each) {
                    int foundId = parseInt(each.get("categoryID"));
                    String foundName = readText(each.get("categoryName"));
                    if (foundId != categoryId && finalName.equalsIgnoreCase(foundName)) {
                        return false;
                    }
                }
            }
        }

        java.util.Map<String, Object> updateData = new java.util.HashMap<>();
        updateData.put("categoryId", categoryId);
        updateData.put("categoryName", finalName);
        updateData.put("categoryStatus", finalStatus);
        return fraCategory.saveUpdateCategory(updateData);
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int parseInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(readText(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
