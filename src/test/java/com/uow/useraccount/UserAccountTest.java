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

    private String gen8DigitPhone() {
        return "7" + String.format("%07d", (int)(Math.random() * 10000000));
    }

    @After
    public void tearDown() {
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
        String uniquePhone = gen8DigitPhone(); // 修复：全局唯一 8 位数字手机号

        Map<String, Object> newAcc = new HashMap<>();
        newAcc.put("username", uniqueName);
        newAcc.put("password", "pass123");
        newAcc.put("fullName", "DAO Lifecycle Test");
        newAcc.put("email", uniqueName + "@example.com");
        newAcc.put("phoneNumber", uniquePhone); 
        newAcc.put("accountStatus", "Active");
        newAcc.put("profileId", 3); // 修复：使用 3，避免 1(User Admin) 产生不可控外键冲突
        
        boolean createResult = dao.saveCreateAccount(newAcc);
        assertTrue("Creation should succeed", createResult);

        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("username", uniqueName);
        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) dao.getSearchAccount(searchParams);
        assertFalse("Search should find the newly created user", searchResults.isEmpty());

        int generatedId = (Integer) searchResults.get(0).get("userId");

        Object viewedUser = dao.getViewAccount(generatedId);
        assertNotNull("View should return the user details", viewedUser);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("userId", generatedId);
        updateData.put("username", uniqueName);
        updateData.put("fullName", "Updated DAO Name");
        updateData.put("email", uniqueName + "@example.com");
        updateData.put("phoneNumber", uniquePhone); // 同一个号
        updateData.put("password", "pass123");
        updateData.put("accountStatus", "Active");
        updateData.put("profileId", 3);
        assertTrue("Update should succeed", dao.saveUpdateAccount(updateData));

        assertTrue("Suspend toggle should succeed", dao.saveSuspendAccount(generatedId, 0));
    }

    @Test
    public void test_Stats_generation_executes_safely() {
        UserAccount dao = new UserAccount();
        assertTrue(dao.getDailyUserStats() instanceof Map);
        assertTrue(dao.getWeeklyUserStats() instanceof Map);
        assertTrue(dao.getMonthlyUserStats() instanceof Map);
        
        assertTrue(dao.getDoneeOptions() instanceof List);
        assertTrue(dao.getFundRaiserOptions() instanceof List);
    }
}