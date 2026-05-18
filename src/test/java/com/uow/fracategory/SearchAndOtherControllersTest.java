package com.uow.fracategory;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class SearchAndOtherControllersTest {

    @Test
    public void test_Search_executes_safely_and_returns_list() {
        SearchFRACategoryController controller = new SearchFRACategoryController();
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("categoryName", "");
        searchParams.put("categoryStatus", "Active");

        Object result = controller.searchCategory(searchParams);
        assertNotNull("Search result should not be null", result);
    }

    @Test
    public void test_View_returns_null_for_invalid_id() {
        ViewFRACategoryController controller = new ViewFRACategoryController();
        assertNull("Viewing non-existent ID should return null", controller.viewCategory(-1));
    }

    @Test
    public void test_Suspend_returns_false_for_missing_id() {
        SuspendFRACategoryController controller = new SuspendFRACategoryController();
        assertFalse("Suspending non-existent ID should return false", controller.suspendCategory(-1));
    }
}