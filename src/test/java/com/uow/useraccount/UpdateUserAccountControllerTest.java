package com.uow.useraccount;

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

public class UpdateUserAccountControllerTest {

    private UpdateUserAccountController updateController;
    private UserAccount dao;
    private final String TEST_PREFIX = "PragmTest_";
    private int existingUserId;
    private String existingPhone;

    private String gen8DigitPhone() {
        return "9" + String.format("%07d", (int)(Math.random() * 10000000));
    }

    @Before
    public void setUp() {
        updateController = new UpdateUserAccountController();
        dao = new UserAccount();

        existingPhone = gen8DigitPhone();
        String uniqueName = TEST_PREFIX + "Update_" + System.currentTimeMillis();
        Map<String, Object> newAcc = new HashMap<>();
        newAcc.put("username", uniqueName);
        newAcc.put("password", "123456");
        newAcc.put("fullName", "To Update");
        newAcc.put("email", uniqueName + "@test.com");
        newAcc.put("phoneNumber", existingPhone); // 修复：使用合法的 8 位
        newAcc.put("accountStatus", "Active");
        newAcc.put("profileId", 3);
        dao.saveCreateAccount(newAcc);

        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put("username", uniqueName);
        List<Map<String, Object>> results = (List<Map<String, Object>>) dao.getSearchAccount(searchParam);
        existingUserId = (Integer) results.get(0).get("userId");
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
    public void test_Update_succeeds_with_valid_data() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("userId", existingUserId);
        updateData.put("fullName", "Updated Name");
        // 更新时如果用不重复的属性也可以通过
        assertTrue("Should return true on successful update", updateController.updateAccount(updateData));
    }

    @Test
    public void test_Update_fails_when_input_is_not_a_map() {
        assertFalse(updateController.updateAccount("Invalid String"));
    }

    @Test
    public void test_Update_fails_when_input_is_null() {
        assertFalse(updateController.updateAccount(null));
    }

    @Test
    public void test_Update_fails_when_user_id_is_missing() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("fullName", "Updated Name");
        assertFalse(updateController.updateAccount(updateData));
    }

    @Test
    public void test_Update_fails_when_user_id_is_invalid() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("userId", -999); // Invalid ID
        assertFalse("Should return false for invalid ID", updateController.updateAccount(updateData));
    }

    @Test
    public void test_Update_fails_when_user_does_not_exist() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("userId", 999999); // Non-existent ID
        updateData.put("fullName", "Updated Name");
        assertFalse("Should return false if user not found", updateController.updateAccount(updateData));
    }

    @Test
    public void test_Update_fails_when_data_conflicts_with_another_existing_user() {
        String userBName = TEST_PREFIX + "Collision_" + System.currentTimeMillis();
        String userBEmail = userBName + "@test.com";
        
        Map<String, Object> userB = new HashMap<>();
        userB.put("username", userBName);
        userB.put("password", "123456");
        userB.put("fullName", "User B");
        userB.put("email", userBEmail);
        userB.put("phoneNumber", gen8DigitPhone()); // 修复：给B分配独立的8位手机号
        userB.put("accountStatus", "Active");
        userB.put("profileId", 3);
        dao.saveCreateAccount(userB);

        Map<String, Object> maliciousUpdateData = new HashMap<>();
        maliciousUpdateData.put("userId", existingUserId); 
        maliciousUpdateData.put("email", userBEmail); // 核心冲突点
        
        assertFalse("Update should fail because the email belongs to another user", 
                    updateController.updateAccount(maliciousUpdateData));
    }
}