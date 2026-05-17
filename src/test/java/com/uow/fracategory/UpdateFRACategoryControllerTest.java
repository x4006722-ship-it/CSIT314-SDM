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
        tearDown();
        updateController = new UpdateFRACategoryController();
        createController = new CreateFRACategoryController();
        searchController = new SearchFRACategoryController();

        existingCategoryName = TEST_PREFIX + "ToUpdate_" + System.currentTimeMillis();
        Map<String, String> newCat = new HashMap<>();
        newCat.put("categoryName", existingCategoryName);
        newCat.put("categoryStatus", "Active");
        createController.createCategory(newCat);

        Map<String, String> searchParam = new HashMap<>();
        searchParam.put("categoryName", existingCategoryName);
        searchParam.put("categoryStatus", "");
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

    // --- 新增：自身重名豁免测试（只改状态不改名字） ---
    @Test
    public void test_Update_succeeds_when_updating_same_category_without_name_change() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", existingCategoryName); // 名字不变
        updateData.put("categoryStatus", "Suspended");

        assertTrue("Should succeed because duplicate check excludes self", updateController.updateCategory(updateData));
    }

    // --- 新增：冲突拦截测试（试图改成别的记录的名字） ---
    @Test
    public void test_Update_throws_exception_when_category_already_exists_for_another_id() {
        // 创建第二个不同的 Category
        String anotherName = TEST_PREFIX + "Another_" + System.currentTimeMillis();
        Map<String, String> anotherCat = new HashMap<>();
        anotherCat.put("categoryName", anotherName);
        anotherCat.put("categoryStatus", "Active");
        createController.createCategory(anotherCat);

        // 试图把第一个的名字改成第二个的名字
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("categoryId", existingCategoryId);
        updateData.put("categoryName", anotherName); // 冲突名字
        updateData.put("categoryStatus", "Active");

        try {
            updateController.updateCategory(updateData);
            fail("Expected exception for duplicate title against another record");
        } catch (IllegalArgumentException e) {
            assertEquals("Category already exists.", e.getMessage());
        }
    }

    // --- 新增：数据格式与NotFound拦截 ---
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
        updateData.put("categoryId", 9999999); // 数据库不存在但大于0的合法数字
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

    // --- 新增：名称和状态验证 ---
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