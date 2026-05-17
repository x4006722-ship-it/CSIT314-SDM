package com.uow.fracategory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class FRACategoryTest {

    private final String TEST_PREFIX = "PragmCatD_";

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
    public void test_Category_DAO_lifecycle_succeeds() {
        FRACategory dao = new FRACategory();
        String uniqueName = TEST_PREFIX + System.currentTimeMillis();

        // 1. Create
        Map<String, String> newCategory = new HashMap<>();
        newCategory.put("categoryName", uniqueName);
        newCategory.put("categoryStatus", "Active");
        assertTrue("Creation should succeed", dao.saveCreateCategory(newCategory));

        // 2. Get ID via exact-match SELECT (reliable across all DB configs)
        int generatedId = 0;
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT category_id FROM fra_category WHERE category_name = ?")) {
            ps.setString(1, uniqueName);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue("Row should be findable by exact name", rs.next());
                generatedId = rs.getInt(1);
            }
        } catch (Exception e) {
            fail("Failed to retrieve generated category ID: " + e.getMessage());
        }

        // 3. Search (test the LIKE-based search also works)
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("categoryName", uniqueName);
        searchParams.put("categoryStatus", "");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) dao.getSearchCategory(searchParams);
        assertFalse("Search should find the newly created category", searchResults.isEmpty());

        // 4. View
        Object viewedCat = dao.getViewCategory(generatedId);
        assertNotNull("View should return the category", viewedCat);

        // 5. Update
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", generatedId);
        updateData.put("categoryName", uniqueName + "_Updated");
        updateData.put("categoryStatus", "Suspended");
        assertTrue("Update should succeed", dao.saveUpdateCategory(updateData));

        // 6. Suspend (Toggle status)
        assertTrue("Suspend toggle should succeed", dao.saveSuspendCategory(generatedId));
    }
}
