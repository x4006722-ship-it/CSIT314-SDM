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

    @Test
    public void test_Creation_succeeds_with_valid_data() {
        Map<String, Object> validData = new HashMap<>();
        validData.put("username", TEST_PREFIX + System.currentTimeMillis());
        validData.put("password", "Password123!");
        validData.put("fullName", "Test User");
        validData.put("email", System.currentTimeMillis() + "@test.com");
        validData.put("phoneNumber", "12345678" + (System.currentTimeMillis() % 100));
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
        data.put("phoneNumber", "12345678");
        assertFalse(controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_password_is_empty() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "");
        data.put("email", "test@test.com");
        data.put("phoneNumber", "12345678");
        assertFalse(controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_password_is_too_short() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "12345"); // Less than 6 chars
        data.put("email", "test@test.com");
        data.put("phoneNumber", "12345678");
        assertFalse("Password should be at least 6 chars", controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_email_is_empty() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "Password123!");
        data.put("email", "");
        data.put("phoneNumber", "12345678");
        assertFalse(controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_email_format_is_invalid() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", TEST_PREFIX + System.currentTimeMillis());
        data.put("password", "Password123!");
        data.put("email", "invalid-email-format");
        data.put("phoneNumber", "12345678");
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
        assertFalse("Phone number should be at least 8 digits", controller.createAccount(data));
    }

    @Test
    public void test_Creation_fails_when_account_is_duplicate() {
        String duplicateName = TEST_PREFIX + "Duplicate";
        
        Map<String, Object> data = new HashMap<>();
        data.put("username", duplicateName);
        data.put("password", "Pass123!");
        data.put("fullName", "Dupe");
        data.put("email", "dupe@test.com");
        data.put("phoneNumber", "99999999");
        data.put("accountStatus", "Active");
        data.put("profileId", 3);

        // First creation succeeds
        assertTrue(controller.createAccount(data));

        // Second creation should be caught by duplicate check and return false
        assertFalse("Should return false because account already exists", controller.createAccount(data));
    }
}
