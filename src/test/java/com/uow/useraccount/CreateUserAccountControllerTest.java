package com.uow.useraccount;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class CreateUserAccountControllerTest {

    private CreateUserAccountController controller;
    private final String TEST_PREFIX = "PragmTest_";

    @Before
    public void setUp() {
        controller = new CreateUserAccountController();
    }

    @After
    public void tearDown() {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM user_account WHERE username LIKE ?")) {
            ps.setString(1, TEST_PREFIX + "%");
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    private String gen8Phone() { return "8" + String.format("%07d", (int)(Math.random() * 10000000)); }

    @Test
    public void test_Creation_succeeds_with_valid_data() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "Pass123");
        data.put("fullName", "Test User");
        data.put("email", System.currentTimeMillis() + "@test.com");
        data.put("phoneNumber", gen8Phone());
        data.put("accountStatus", "Active");
        data.put("profileId", 3);
        
        assertTrue("Controller should return true for completely valid data", controller.createAccount(data));
    }

    @Test(expected = NullPointerException.class)
    public void test_Creation_fails_when_input_is_null() {
        // 由于边界层(Boundary)会拦截null，Controller默认传入的是实例化好的Map。
        // 如果硬传null，应当抛出 NullPointerException
        controller.createAccount(null);
    }

    // 格式校验（密码长度、手机号格式、空字段）已经移交 Boundary 层处理，
    // 因此 Controller 层不再需要为其编写单元测试。

    @Test(expected = IllegalArgumentException.class)
    public void test_Creation_fails_when_account_is_duplicate() {
        String name = TEST_PREFIX + "Dup";
        Map<String, Object> data = new HashMap<>();
        data.put("username", name);
        data.put("password", "Pass123");
        data.put("fullName", "User");
        data.put("email", name + "@t.com");
        data.put("phoneNumber", gen8Phone());
        data.put("accountStatus", "Active");
        data.put("profileId", 3);

        controller.createAccount(data); // 第一次成功落库
        controller.createAccount(data); // 第二次触发 Entity 查重并由 Controller 抛出异常
    }
}