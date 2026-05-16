package com.uow.fracategory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class CreateFRACategoryControllerTest {

    private CreateFRACategoryController controller;
    private final String TEST_PREFIX = "PragmaticTest_";

    @Before
    public void setUp() {
        controller = new CreateFRACategoryController();
    }

    @After
    public void tearDown() {
        // Clean up test data to keep the database clean
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM fra_category WHERE category_name LIKE ?")) {
            ps.setString(1, TEST_PREFIX + "%");
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    @Test
    public void test_Creation_succeeds_with_valid_data() {
        Map<String, String> validData = new HashMap<>();
        validData.put("categoryName", TEST_PREFIX + System.currentTimeMillis());
        validData.put("categoryStatus", "Active");

        assertTrue(controller.createCategory(validData));
    }

    @Test
    public void test_Creation_throws_exception_when_name_is_empty() {
        Map<String, String> invalidData = new HashMap<>();
        invalidData.put("categoryName", "   ");
        invalidData.put("categoryStatus", "Active");

        try {
            controller.createCategory(invalidData);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Category name cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void test_Creation_throws_exception_when_status_is_invalid() {
        Map<String, String> invalidData = new HashMap<>();
        invalidData.put("categoryName", TEST_PREFIX + "InvalidStatus");
        invalidData.put("categoryStatus", "HackedStatus");

        try {
            controller.createCategory(invalidData);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid status format.", e.getMessage());
        }
    }

    @Test
    public void test_Creation_throws_exception_when_category_already_exists() {
        String duplicateName = TEST_PREFIX + "Duplicate";
        Map<String, String> data = new HashMap<>();
        data.put("categoryName", duplicateName);
        data.put("categoryStatus", "Active");

        // First creation should succeed
        controller.createCategory(data);

        // Second creation with the same name should fail
        try {
            controller.createCategory(data);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Category already exists.", e.getMessage());
        }
    }
}