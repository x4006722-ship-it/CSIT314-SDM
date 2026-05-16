package com.uow.fracategory;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class SearchAndOtherControllersTest {

    @Test
    public void test_Search_executes_safely_and_returns_list() {
        SearchFRACategoryController controller = new SearchFRACategoryController();
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("categoryName", "");
        searchParams.put("categoryStatus", "Active");

        Object result = controller.searchCategory(searchParams);
        assertNotNull(result);
    }

    @Test
    public void test_Search_throws_exception_when_status_is_invalid() {
        SearchFRACategoryController controller = new SearchFRACategoryController();
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("categoryName", "Test");
        searchParams.put("categoryStatus", "InvalidHackedStatus");

        try {
            controller.searchCategory(searchParams);
            fail("Expected exception for invalid status format");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid status format.", e.getMessage());
        }
    }

    @Test
    public void test_View_executes_safely() {
        ViewFRACategoryController controller = new ViewFRACategoryController();
        // Just verify it doesn't crash on non-existent IDs
        assertNull(controller.viewCategory(-9999));
    }

    @Test
    public void test_Suspend_returns_false_for_invalid_id() {
        SuspendFRACategoryController controller = new SuspendFRACategoryController();
        assertFalse(controller.suspendCategory(-9999));
    }
}
