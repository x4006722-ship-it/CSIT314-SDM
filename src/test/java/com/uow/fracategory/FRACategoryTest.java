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

public class FRACategoryTest {

    private final String TEST_PREFIX = "PragmaticTest_DAO_";

    @After
    public void tearDown() {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM fra_category WHERE category_name LIKE ?")) {
            ps.setString(1, TEST_PREFIX + "%");
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    @Test
    public void test_Category_DAO_lifecycle_succeeds() {
        FRACategory dao = new FRACategory();
        String uniqueName = TEST_PREFIX + System.currentTimeMillis();

        // 1. Create
        Map<String, String> newCategory = new HashMap<>();
        newCategory.put("categoryName", uniqueName);
        newCategory.put("categoryStatus", "Active");
        assertTrue("Creation should succeed", dao.saveCreateCategory(newCategory));

        // 2. Search
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("categoryName", uniqueName);
        searchParams.put("categoryStatus", "");
        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) dao.getSearchCategory(searchParams);
        assertFalse("Search should find the newly created category", searchResults.isEmpty());

        int generatedId = (Integer) searchResults.get(0).get("categoryID");

        // 3. View
        Object viewedCat = dao.getViewCategory(generatedId);
        assertNotNull("View should return the category", viewedCat);

        // 4. Update
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", generatedId);
        updateData.put("categoryName", uniqueName + "_Updated");
        updateData.put("categoryStatus", "Suspended");
        assertTrue("Update should succeed", dao.saveUpdateCategory(updateData));

        // 5. Suspend (Toggle status)
        assertTrue("Suspend toggle should succeed", dao.saveSuspendCategory(generatedId));
    }
}