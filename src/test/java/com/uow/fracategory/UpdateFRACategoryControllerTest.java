package com.uow.fracategory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class UpdateFRACategoryControllerTest {

    private UpdateFRACategoryController updateController;
    private CreateFRACategoryController createController;
    private SearchFRACategoryController searchController;
    private final String TEST_PREFIX = "PragmaticUpd_";
    private int existingCategoryId;
    private String existingCategoryName;

    @Before
    public void setUp() {
        updateController = new UpdateFRACategoryController();
        createController = new CreateFRACategoryController();
        searchController = new SearchFRACategoryController();

        existingCategoryName = TEST_PREFIX + "ToUpdate_" + System.currentTimeMillis();
        Map<String, Object> newCat = new HashMap<>();
        newCat.put("categoryName", existingCategoryName);
        newCat.put("categoryStatus", "Active");
        createController.createCategory(newCat);

        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put("categoryName", existingCategoryName);
        
        @SuppressWarnings("unchecked")
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
    public void test_Update_succeeds_when_updating_same_category_without_name_change() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", existingCategoryName); 
        updateData.put("categoryStatus", "Suspended");

        assertTrue("Should succeed because duplicate check excludes self ID", updateController.updateCategory(updateData));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_Update_throws_exception_when_category_already_exists_for_another_id() {
        String anotherName = TEST_PREFIX + "Another_" + System.currentTimeMillis();
        Map<String, Object> anotherCat = new HashMap<>();
        anotherCat.put("categoryName", anotherName);
        anotherCat.put("categoryStatus", "Active");
        createController.createCategory(anotherCat);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", anotherName); // Conflict!
        updateData.put("categoryStatus", "Active");

        updateController.updateCategory(updateData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_Update_throws_exception_when_category_not_found() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", 999999); 
        updateData.put("categoryName", "Random");
        updateData.put("categoryStatus", "Active");
        
        updateController.updateCategory(updateData);
    }
}