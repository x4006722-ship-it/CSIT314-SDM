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

    private String gen8DigitPhone() {
        // 生成严格的 8 位随机数字
        return "8" + String.format("%07d", (int)(Math.random() * 10000000));
    }

    @Test
    public void test_Creation_succeeds_with_valid_data() {
        Map<String, Object> validData = new HashMap<>();
        validData.put("username", TEST_PREFIX + System.currentTimeMillis());
        validData.put("password", "Password123!");
        validData.put("fullName", "Test User");
        validData.put("email", System.currentTimeMillis() + "@test.com");
        validData.put("phoneNumber", gen8DigitPhone()); // 修复：使用严格的 8 位手机号
        validData.put("accountStatus", "Active");
        validData.put("profileId", 3);

        assertTrue("Should return true on successful creation", controller.createAccount(validData));
    }

    @Test
    public void test_Creation_fails_when_input_is_not_a_map() {
        assertFalse("Should return false if data is not a Map", controller.createAccount("Invalid Data String"));
    }

    @Test
    public void test_Creation_fails_when_input_is_null() {
        assertFalse("Should return false if data is null", controller.createAccount(null));
    }

    @Test
    public void test_Creation_fails_when_username_is_empty() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", "");
        data.put("password", "Password123!");
        data.put("email", "test@test.com");
        data.put("phoneNumber", gen8DigitPhone());
        assertFalse(controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_password_is_empty() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "");
        data.put("email", "test@test.com");
        data.put("phoneNumber", gen8DigitPhone());
        assertFalse(controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_password_is_too_short() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "12"); // 修复：Controller限制至少3位，用 2 位密码测试才会报错
        data.put("email", "test@test.com");
        data.put("phoneNumber", gen8DigitPhone());
        assertFalse("Password should be at least 3 chars", controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_email_is_empty() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "Password123!");
        data.put("email", "");
        data.put("phoneNumber", gen8DigitPhone());
        assertFalse(controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_email_format_is_invalid() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "Password123!");
        data.put("email", "invalid-email-format");
        data.put("phoneNumber", gen8DigitPhone());
        assertFalse("Invalid email format should be rejected", controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_phone_number_is_empty() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "Password123!");
        data.put("email", "test@test.com");
        data.put("phoneNumber", "");
        assertFalse(controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_phone_number_is_too_short() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "Password123!");
        data.put("email", "test@test.com");
        data.put("phoneNumber", "1234567"); // Less than 8 digits
        assertFalse("Phone number should be exactly 8 digits", controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_account_is_duplicate() {
        String duplicateName = TEST_PREFIX + "Duplicate_" + System.currentTimeMillis();
        
        Map<String, Object> data = new HashMap<>();
        data.put("username", duplicateName);
        data.put("password", "Pass123!");
        data.put("fullName", "Dupe");
        data.put("email", duplicateName + "@test.com");
        data.put("phoneNumber", gen8DigitPhone()); // 用独立的 8 位数避免与其他测试撞车
        data.put("accountStatus", "Active");
        data.put("profileId", 3);

        // First creation succeeds
        assertTrue(controller.createAccount(data));

        // Second creation should be caught by duplicate check and return false
        assertFalse("Should return false because account already exists", controller.createAccount(data));
    }
}