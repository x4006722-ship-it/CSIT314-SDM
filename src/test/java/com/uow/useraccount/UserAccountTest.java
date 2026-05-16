package com.uow.useraccount;

import org.junit.After;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class UserAccountTest {

    private final String TEST_PREFIX = "PragmDAO_";

    @After
    public void tearDown() {
        // 清理测试产生的数据
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM user_account WHERE username LIKE ?")) {
            ps.setString(1, TEST_PREFIX + "%");
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    @Test
    public void test_UserAccount_DAO_lifecycle_succeeds() {
        UserAccount dao = new UserAccount();
        String uniqueName = TEST_PREFIX + System.currentTimeMillis();

        // 1. Create - 确保所有字段都填上，且 profileId 使用最通用的 1
        Map<String, Object> newAcc = new HashMap<>();
        newAcc.put("username", uniqueName);
        newAcc.put("password", "pass123");
        newAcc.put("fullName", "DAO Lifecycle Test");
        newAcc.put("email", uniqueName + "@example.com");
        newAcc.put("phoneNumber", "99998888");
        newAcc.put("accountStatus", "Active");
        newAcc.put("profileId", 1); // <--- 尝试使用 ID 1，通常这是管理员或默认角色
        
        boolean createResult = dao.saveCreateAccount(newAcc);
        assertTrue("Creation should succeed", createResult);

        // 2. Search
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("username", uniqueName);
        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) dao.getSearchAccount(searchParams);
        assertFalse("Search should find the newly created user", searchResults.isEmpty());

        int generatedId = (Integer) searchResults.get(0).get("userId");

        // 3. View
        Object viewedUser = dao.getViewAccount(generatedId);
        assertNotNull("View should return the user details", viewedUser);

        // 4. Update
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("userId", generatedId);
        updateData.put("username", uniqueName);
        updateData.put("fullName", "Updated DAO Name");
        updateData.put("email", uniqueName + "@example.com");
        updateData.put("phoneNumber", "99998888");
        updateData.put("password", "pass123");
        updateData.put("accountStatus", "Active");
        updateData.put("profileId", 1);
        assertTrue("Update should succeed", dao.saveUpdateAccount(updateData));

        // 5. Suspend
        assertTrue("Suspend toggle should succeed", dao.saveSuspendAccount(generatedId, 0));
    }

    @Test
    public void test_Stats_generation_executes_safely() {
        UserAccount dao = new UserAccount();
        // 验证统计方法不会崩溃
        assertTrue(dao.getDailyUserStats() instanceof Map);
        assertTrue(dao.getWeeklyUserStats() instanceof Map);
        assertTrue(dao.getMonthlyUserStats() instanceof Map);
        
        // 验证下拉框获取不会崩溃
        assertTrue(dao.getDoneeOptions() instanceof List);
        assertTrue(dao.getFundRaiserOptions() instanceof List);
    }
}