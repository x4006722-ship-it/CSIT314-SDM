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

public class UpdateFRACategoryControllerTest {

    private UpdateFRACategoryController updateController;
    private CreateFRACategoryController createController;
    private final String TEST_PREFIX = "PragmCatU_";
    private int existingCategoryId;
    private String existingCategoryName;

    @Before
    public void setUp() {
        tearDown();
        updateController = new UpdateFRACategoryController();
        createController = new CreateFRACategoryController();

        existingCategoryName = TEST_PREFIX + "ToUpdate_" + System.currentTimeMillis();
        Map<String, String> newCat = new HashMap<>();
        newCat.put("categoryName", existingCategoryName);
        newCat.put("categoryStatus", "Active");
        createController.createCategory(newCat);

        // Get ID via exact-match SELECT (avoids LIKE wildcard issues)
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT category_id FROM fra_category WHERE category_name = ?")) {
            ps.setString(1, existingCategoryName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) existingCategoryId = rs.getInt(1);
            }
        } catch (Exception e) {
            existingCategoryId = 0;
        }
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
    public void test_Update_succeeds_when_updating_same_category_without_name_change() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", existingCategoryName);
        updateData.put("categoryStatus", "Suspended");

        assertTrue("Should succeed because duplicate check excludes self", updateController.updateCategory(updateData));
    }

    @Test
    public void test_Update_throws_exception_when_category_already_exists_for_another_id() {
        String anotherName = TEST_PREFIX + "Another_" + System.currentTimeMillis();
        Map<String, String> anotherCat = new HashMap<>();
        anotherCat.put("categoryName", anotherName);
        anotherCat.put("categoryStatus", "Active");
        createController.createCategory(anotherCat);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", anotherName);
        updateData.put("categoryStatus", "Active");

        try {
            updateController.updateCategory(updateData);
            fail("Expected exception for duplicate title against another record");
        } catch (IllegalArgumentException e) {
            assertEquals("Category already exists.", e.getMessage());
        }
    }

    @Test
    public void test_Update_throws_exception_for_invalid_data_format() {
        try {
            updateController.updateCategory(null);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid data format.", e.getMessage());
        }
    }

    @Test
    public void test_Update_throws_exception_when_category_not_found() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", 9999999);
        updateData.put("categoryName", "Valid Name");
        updateData.put("categoryStatus", "Active");

        try {
            updateController.updateCategory(updateData);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Category not found.", e.getMessage());
        }
    }

    @Test
    public void test_Update_throws_exception_when_category_id_is_invalid() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", -999);

        try {
            updateController.updateCategory(updateData);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid category ID.", e.getMessage());
        }
    }

    @Test
    public void test_Update_throws_exception_when_name_is_empty() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", "   ");
        updateData.put("categoryStatus", "Active");

        try {
            updateController.updateCategory(updateData);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Category name cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void test_Update_throws_exception_when_status_is_missing() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", "Valid Name");
        updateData.put("categoryStatus", "");

        try {
            updateController.updateCategory(updateData);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Category status cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void test_Update_throws_exception_when_status_is_invalid() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", "Valid Name");
        updateData.put("categoryStatus", "Hacked");

        try {
            updateController.updateCategory(updateData);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid status format.", e.getMessage());
        }
    }
}
