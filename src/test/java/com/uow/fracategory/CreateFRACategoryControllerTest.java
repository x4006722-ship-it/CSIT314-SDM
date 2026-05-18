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
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM fra_category WHERE category_name LIKE ?")) {
            ps.setString(1, TEST_PREFIX + "%");
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    @Test
    public void test_Creation_succeeds_with_valid_data() {
        Map<String, Object> validData = new HashMap<>();
        validData.put("categoryName", TEST_PREFIX + System.currentTimeMillis());
        validData.put("categoryStatus", "Active");

        assertTrue("Controller should return true for valid domain data", controller.createCategory(validData));
    }

    /**
     * Business Rule Test: Duplicates are not allowed.
     * This remains in the Controller as it requires a database state check.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_Creation_throws_exception_when_category_already_exists() {
        String duplicateName = TEST_PREFIX + "Duplicate";
        Map<String, Object> data = new HashMap<>();
        data.put("categoryName", duplicateName);
        data.put("categoryStatus", "Active");

        controller.createCategory(data); // First creation succeeds
        controller.createCategory(data); // Second creation must throw exception
    }
}