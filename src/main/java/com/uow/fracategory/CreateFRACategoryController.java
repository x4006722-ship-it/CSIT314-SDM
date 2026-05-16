package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Controller
public class CreateFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    // 保持返回 boolean 不变！
    public boolean createCategory(Object newCategoryData) {
        if (!(newCategoryData instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Invalid data format.");
        }

        String newName = readText(map.get("categoryName"));
        String newStatus = readText(map.get("categoryStatus"));

        // 1. 边界检查
        // 1. 如果用户忘记填名字（最常见的情况）
        if (newName.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        
        // 2. 如果有人恶意用黑客工具发了空状态（拦截小人）
        if (newStatus.isBlank()) {
            throw new IllegalArgumentException("Category status is missing.");
        }
        
        // 3. 如果有人恶意发了诸如 "Hacked" 这种非法状态
        if (!"Active".equalsIgnoreCase(newStatus) && !"Suspended".equalsIgnoreCase(newStatus)) {
            throw new IllegalArgumentException("Invalid status format.");
        }

        // 2. 商业逻辑：查重
        Map<String, Object> searchData = new HashMap<>();
        searchData.put("categoryName", newName);
        Object existingRows = fraCategory.getSearchCategory(searchData);

        if (existingRows instanceof List<?> rows) {
            for (Object row : rows) {
                if (row instanceof Map<?, ?> r) {
                    String categoryName = readText(r.get("categoryName"));
                    if (newName.equalsIgnoreCase(categoryName)) {
                        // 【核心】直接抛出英文错误，中断流程！
                        throw new IllegalArgumentException("Category already exists.");
                    }
                }
            }
        }

        // 3. 一切合规，执行保存
        return fraCategory.saveCreateCategory(newCategoryData);
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}