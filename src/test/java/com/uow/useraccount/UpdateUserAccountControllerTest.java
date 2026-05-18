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
    private final String TEST_PREFIX = "PragmUpd_";
    private int existingUserId;

    @Before
    public void setUp() {
        updateController = new UpdateUserAccountController();
        dao = new UserAccount();
        String name = TEST_PREFIX + System.currentTimeMillis();
        Map<String, Object> acc = new HashMap<>();
        acc.put("username", name); 
        acc.put("password", "123");
        acc.put("fullName", "User"); 
        acc.put("email", name + "@t.com");
        acc.put("phoneNumber", "11112222"); 
        acc.put("accountStatus", "Active");
        acc.put("profileId", 3);
        dao.saveCreateAccount(acc);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> res = (List<Map<String, Object>>) dao.getSearchAccount(Map.of("username", name));
        existingUserId = (Integer) res.get(0).get("userId");
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
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", "New Name"); // 只更新全名，其余字段 Fallback 回旧数据
        
        // 【修复】：方法签名更新为传入 userId 和 Map
        assertTrue(updateController.updateAccount(existingUserId, data));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_Update_fails_when_user_id_invalid() {
        // 期待抛出：Account not found in database.
        updateController.updateAccount(-1, new HashMap<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_Update_fails_when_email_duplicate() {
        // 创建另一个用户并尝试把当前用户邮箱改成和他一样
        String other = TEST_PREFIX + "Other";
        dao.saveCreateAccount(Map.of(
            "username", other, "password", "123", "fullName", "B", 
            "email", "b@t.com", "phoneNumber", "88887777", 
            "accountStatus", "Active", "profileId", 3
        ));
        
        Map<String, Object> data = new HashMap<>();
        data.put("email", "b@t.com"); // 触发邮箱冲突
        
        // 【修复】：方法签名更新
        updateController.updateAccount(existingUserId, data);
    }
}