package com.uow.fracategory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class SearchAndOtherControllersTest {

    private final String TEST_PREFIX = "PragmCatS_";

    @Before
    public void setUp() {
        tearDown();
    }

    @After
    public void tearDown() {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM fra_category WHERE category_name LIKE ?")) {
            ps.setString(1, TEST_PREFIX + "%");
            ps.executeUpdate();
        } catch (Exception e) {}
    }

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
    public void test_Search_executes_safely_with_null_or_invalid_type() {
        SearchFRACategoryController controller = new SearchFRACategoryController();
        Object result = controller.searchCategory(null);
        assertNotNull("Should safely return an empty list or execute without crashing", result);
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
        assertNull(controller.viewCategory(-9999));
    }

    @Test
    public void test_Suspend_returns_false_for_invalid_id() {
        SuspendFRACategoryController controller = new SuspendFRACategoryController();
        assertFalse(controller.suspendCategory(-9999));
    }

    @Test
    public void test_Suspend_succeeds_for_valid_id() {
        CreateFRACategoryController createController = new CreateFRACategoryController();
        String catName = TEST_PREFIX + "ToSuspend_" + System.currentTimeMillis();
        Map<String, String> newCat = new HashMap<>();
        newCat.put("categoryName", catName);
        newCat.put("categoryStatus", "Active");
        createController.createCategory(newCat);

        // Get ID via exact-match SELECT
        int validId = 0;
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT category_id FROM fra_category WHERE category_name = ?")) {
            ps.setString(1, catName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) validId = rs.getInt(1);
            }
        } catch (Exception e) {
            fail("Failed to retrieve category ID: " + e.getMessage());
        }

        assertTrue("validId should be positive", validId > 0);
        SuspendFRACategoryController suspendController = new SuspendFRACategoryController();
        assertTrue("Suspend should succeed for a valid existing ID", suspendController.suspendCategory(validId));
    }
}
