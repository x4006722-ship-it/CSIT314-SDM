package com.uow.fracategory;

import org.springframework.stereotype.Controller;

@Controller
public class SuspendFRACategoryController {

    private final FRACategory fraCategory = new FRACategory();

    public boolean suspendCategory(int categoryId) {
        Object existing = fraCategory.getViewCategory(categoryId);
        if (!(existing instanceof java.util.Map<?, ?>)) {
            return false;
        }
        return fraCategory.saveSuspendCategory(categoryId);
    }
}
