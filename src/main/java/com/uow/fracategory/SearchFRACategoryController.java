// package com.uow.fracategory;

// import org.springframework.stereotype.Controller;

// @Controller
// public class SearchFRACategoryController {

//     private final FRACategory fraCategory = new FRACategory();

//     public Object searchCategory(Object searchCategoryData) {
//         return fraCategory.getSearchCategory(searchCategoryData);
//     }
// }
package com.uow.fracategory;

import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class SearchFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public Object searchCategory(Object searchCategoryData) {
        // 1. 在业务层集中进行状态格式的校验
        if (searchCategoryData instanceof Map<?, ?> map) {
            String categoryStatus = readText(map.get("categoryStatus"));
            
            if (!categoryStatus.isBlank() && 
                !"Active".equalsIgnoreCase(categoryStatus) && 
                !"Suspended".equalsIgnoreCase(categoryStatus)) {
                // 抛出异常，统一处理！
                throw new IllegalArgumentException("Invalid status format.");
            }
        }
        
        // 2. 校验通过，交给底层 DAO
        return fraCategory.getSearchCategory(searchCategoryData);
    }

    // 辅助方法
    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
