// package com.uow.fracategory;

// import org.springframework.stereotype.Controller;

// @Controller
// public class UpdateFRACategoryController {

//     private final FRACategory fraCategory = new FRACategory();

//     public boolean updateCategory(Object updatedCategoryData) {
//         if (!(updatedCategoryData instanceof java.util.Map<?, ?> map)) {
//             return false;
//         }

//         int categoryId = parseInt(map.get("categoryId"));
//         if (categoryId <= 0) {
//             return false;
//         }

//         Object currentCategory = fraCategory.getViewCategory(categoryId);
//         if (!(currentCategory instanceof java.util.Map<?, ?> currentMap)) {
//             return false;
//         }

//         String incomingName = readText(map.get("categoryName"));
//         String incomingStatus = readText(map.get("categoryStatus"));
//         String finalName = incomingName.isBlank() ? readText(currentMap.get("categoryName")) : incomingName;
//         String finalStatus = incomingStatus.isBlank() ? readText(currentMap.get("categoryStatus")) : incomingStatus;

//         java.util.Map<String, Object> searchData = new java.util.HashMap<>();
//         searchData.put("categoryName", finalName);
//         Object duplicatedRows = fraCategory.getSearchCategory(searchData);
//         if (duplicatedRows instanceof java.util.List<?> rows) {
//             for (Object row : rows) {
//                 if (row instanceof java.util.Map<?, ?> each) {
//                     int foundId = parseInt(each.get("categoryID"));
//                     String foundName = readText(each.get("categoryName"));
//                     if (foundId != categoryId && finalName.equalsIgnoreCase(foundName)) {
//                         return false;
//                     }
//                 }
//             }
//         }

//         java.util.Map<String, Object> updateData = new java.util.HashMap<>();
//         updateData.put("categoryId", categoryId);
//         updateData.put("categoryName", finalName);
//         updateData.put("categoryStatus", finalStatus);
//         return fraCategory.saveUpdateCategory(updateData);
//     }

//     private String readText(Object value) {
//         return value == null ? "" : String.valueOf(value).trim();
//     }

//     private int parseInt(Object value) {
//         if (value instanceof Number number) {
//             return number.intValue();
//         }
//         try {
//             return Integer.parseInt(readText(value));
//         } catch (NumberFormatException e) {
//             return 0;
//         }
//     }
// }
// package com.uow.fracategory;

// import org.springframework.stereotype.Controller;
// import java.util.Map;
// import java.util.HashMap;
// import java.util.List;

// @Controller
// public class UpdateFRACategoryController {

//     private final FRACategory fraCategory = new FRACategory();

//     // 保持返回 boolean 不变！
//     public boolean updateCategory(Object updatedCategoryData) {
//         if (!(updatedCategoryData instanceof Map<?, ?> map)) {
//             throw new IllegalArgumentException("Invalid data format.");
//         }

//         int categoryId = parseInt(map.get("categoryId"));
//         if (categoryId <= 0) {
//             throw new IllegalArgumentException("Invalid category ID.");
//         }

//         Object currentCategory = fraCategory.getViewCategory(categoryId);
//         if (!(currentCategory instanceof Map<?, ?> currentMap)) {
//             throw new IllegalArgumentException("Category not found.");
//         }

//         String incomingName = readText(map.get("categoryName"));
//         String incomingStatus = readText(map.get("categoryStatus"));
//         String finalName = incomingName.isBlank() ? readText(currentMap.get("categoryName")) : incomingName;
//         String finalStatus = incomingStatus.isBlank() ? readText(currentMap.get("categoryStatus")) : incomingStatus;

//         if (!"Active".equalsIgnoreCase(finalStatus) && !"Suspended".equalsIgnoreCase(finalStatus)) {
//             throw new IllegalArgumentException("Invalid status format.");
//         }

//         // 查重逻辑（排除自身）
//         Map<String, Object> searchData = new HashMap<>();
//         searchData.put("categoryName", finalName);
//         Object duplicatedRows = fraCategory.getSearchCategory(searchData);
//         if (duplicatedRows instanceof List<?> rows) {
//             for (Object row : rows) {
//                 if (row instanceof Map<?, ?> each) {
//                     int foundId = parseInt(each.get("categoryID"));
//                     String foundName = readText(each.get("categoryName"));
//                     if (foundId != categoryId && finalName.equalsIgnoreCase(foundName)) {
//                         // 【核心】抛出重名错误
//                         throw new IllegalArgumentException("Category already exists.");
//                     }
//                 }
//             }
//         }

//         Map<String, Object> updateData = new HashMap<>();
//         updateData.put("categoryId", categoryId);
//         updateData.put("categoryName", finalName);
//         updateData.put("categoryStatus", finalStatus);
//         return fraCategory.saveUpdateCategory(updateData);
//     }

//     private String readText(Object value) {
//         return value == null ? "" : String.valueOf(value).trim();
//     }

//     private int parseInt(Object value) {
//         if (value instanceof Number number) {
//             return number.intValue();
//         }
//         try {
//             return Integer.parseInt(readText(value));
//         } catch (NumberFormatException e) {
//             return 0;
//         }
//     }
// }
package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Controller
public class UpdateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    // 保持返回 boolean 不变！
    public boolean updateCategory(Object updatedCategoryData) {
        if (!(updatedCategoryData instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Invalid data format.");
        }

        int categoryId = parseInt(map.get("categoryId"));
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }

        Object currentCategory = fraCategory.getViewCategory(categoryId);
        if (!(currentCategory instanceof Map<?, ?> currentMap)) {
            throw new IllegalArgumentException("Category not found.");
        }

        String incomingName = readText(map.get("categoryName"));
        String incomingStatus = readText(map.get("categoryStatus"));

        // ==========================================
        // 1. 精准边界检查：不再静默使用旧值，直接报错拦截！
        // ==========================================
        if (incomingName.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        if (incomingStatus.isBlank()) {
            throw new IllegalArgumentException("Category status cannot be empty.");
        }
        if (!"Active".equalsIgnoreCase(incomingStatus) && !"Suspended".equalsIgnoreCase(incomingStatus)) {
            throw new IllegalArgumentException("Invalid status format.");
        }

        // ==========================================
        // 2. 查重逻辑（排除自身）
        // ==========================================
        Map<String, Object> searchData = new HashMap<>();
        searchData.put("categoryName", incomingName);
        Object duplicatedRows = fraCategory.getSearchCategory(searchData);
        if (duplicatedRows instanceof List<?> rows) {
            for (Object row : rows) {
                if (row instanceof Map<?, ?> each) {
                    int foundId = parseInt(each.get("categoryID"));
                    String foundName = readText(each.get("categoryName"));
                    
                    // 如果发现同名，且那个同名的 ID 不是我们正在修改的这条数据的 ID
                    if (foundId != categoryId && incomingName.equalsIgnoreCase(foundName)) {
                        throw new IllegalArgumentException("Category already exists.");
                    }
                }
            }
        }

        // ==========================================
        // 3. 一切合规，执行更新
        // ==========================================
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", categoryId);
        updateData.put("categoryName", incomingName);
        updateData.put("categoryStatus", incomingStatus);
        
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