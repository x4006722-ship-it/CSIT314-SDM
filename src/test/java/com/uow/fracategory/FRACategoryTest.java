package com.uow.fracategory;

import org.junit.After;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

/**
 * Unit tests for the Entity Layer (FRACategory).
 * These tests verify that raw database operations (CRUD) are functioning 
 * correctly regardless of business logic.
 */
public class FRACategoryTest {

    private final String TEST_PREFIX = "PragmaticTest_DAO_";

    @After
    public void tearDown() {
        // Cleanup: Remove all test records created during execution
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM fra_category WHERE category_name LIKE ?")) {
            ps.setString(1, TEST_PREFIX + "%");
            ps.executeUpdate();
        } catch (Exception e) {
            // Silently ignore cleanup errors to prevent test pollution
        }
    }

    @Test
    public void test_Category_DAO_lifecycle_succeeds() {
        FRACategory dao = new FRACategory();
        String uniqueName = TEST_PREFIX + System.currentTimeMillis();

        // 1. Create: Using Map<String, Object> to match the refactored Entity method signature
        Map<String, Object> newCategory = new HashMap<>();
        newCategory.put("categoryName", uniqueName);
        newCategory.put("categoryStatus", "Active");
        assertTrue("DAO save operation should return true for valid INSERT", dao.saveCreateCategory(newCategory));

        // 2. Search: Verify that the dynamic search can find the persistent data
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("categoryName", uniqueName);
        searchParams.put("categoryStatus", "");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) dao.getSearchCategory(searchParams);
        assertFalse("Search result must not be empty after creation", searchResults.isEmpty());

        // Extract ID for the next steps
        int generatedId = (Integer) searchResults.get(0).get("categoryID");

        // 3. View: Verify retrieval by ID returns the correct Map structure
        Object viewedCat = dao.getViewCategory(generatedId);
        assertNotNull("View should return a valid Map containing category details", viewedCat);
        assertTrue("The returned result from View must be a Map", viewedCat instanceof Map);

        // 4. Update: Verify that the UPDATE SQL executes successfully
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", generatedId);
        updateData.put("categoryName", uniqueName + "_Updated");
        updateData.put("categoryStatus", "Suspended");
        assertTrue("DAO update operation should return true for valid record ID", dao.saveUpdateCategory(updateData));

        // 5. Suspend: Test the CASE WHEN toggle logic in the SQL
        // Current status is 'Suspended' (from step 4), so toggle should make it 'Active'
        assertTrue("Suspend toggle should succeed for existing ID", dao.saveSuspendCategory(generatedId));
        
        // Final Assertion: Verify the toggle worked correctly
        @SuppressWarnings("unchecked")
        Map<String, Object> finalData = (Map<String, Object>) dao.getViewCategory(generatedId);
        assertEquals("Status should have toggled from 'Suspended' back to 'Active'", "Active", finalData.get("categoryStatus"));
    }
}