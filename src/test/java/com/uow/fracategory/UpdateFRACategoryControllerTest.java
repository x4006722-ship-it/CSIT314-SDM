package com.uow.fracategory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class UpdateFRACategoryControllerTest {

    private UpdateFRACategoryController updateController;
    private CreateFRACategoryController createController;
    private SearchFRACategoryController searchController;
    private final String TEST_PREFIX = "PragmaticTest_";
    private int existingCategoryId;
    private String existingCategoryName;

    @Before
    public void setUp() {
        updateController = new UpdateFRACategoryController();
        createController = new CreateFRACategoryController();
        searchController = new SearchFRACategoryController();

        // Dynamically create a real category to test the update logic
        existingCategoryName = TEST_PREFIX + "ToUpdate_" + System.currentTimeMillis();
        Map<String, String> newCat = new HashMap<>();
        newCat.put("categoryName", existingCategoryName);
        newCat.put("categoryStatus", "Active");
        createController.createCategory(newCat);

        // Fetch its ID
        Map<String, String> searchParam = new HashMap<>();
        searchParam.put("categoryName", existingCategoryName);
        searchParam.put("categoryStatus", "");
        List<Map<String, Object>> results = (List<Map<String, Object>>) searchController.searchCategory(searchParam);
        existingCategoryId = (Integer) results.get(0).get("categoryID");
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
    public void test_Update_succeeds_with_valid_data() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", existingCategoryName + "_Updated");
        updateData.put("categoryStatus", "Suspended");

        assertTrue(updateController.updateCategory(updateData));
    }

    @Test
    public void test_Update_throws_exception_when_category_id_is_invalid() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", -999); // Invalid ID
        
        try {
            updateController.updateCategory(updateData);
            fail("Expected exception for invalid ID");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid category ID.", e.getMessage());
        }
    }

    @Test
    public void test_Update_throws_exception_when_status_is_missing() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", "Valid Name");
        updateData.put("categoryStatus", ""); // Missing status
        
        try {
            updateController.updateCategory(updateData);
            fail("Expected exception for empty status");
        } catch (IllegalArgumentException e) {
            assertEquals("Category status cannot be empty.", e.getMessage());
        }
    }
}