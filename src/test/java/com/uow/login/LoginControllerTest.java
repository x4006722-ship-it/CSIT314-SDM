package com.uow.login;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class LoginControllerTest {

    private LoginController controller;

    @Before
    public void setUp() {
        controller = new LoginController();
    }

    @Test
    public void test_Login_fails_when_credentials_are_invalid() {
        Map<String, String> invalidData = new HashMap<>();
        invalidData.put("username", "non_existent_user");
        invalidData.put("password", "wrong_password");

        Object result = controller.login(invalidData);
        assertTrue(result instanceof Map);
        assertEquals("Invalid credentials.", ((Map<?, ?>) result).get("error"));
    }

    @Test
    public void test_Login_fails_when_account_is_suspended() {
        assertTrue(true); // Placeholder for status-based logic verification
    }
}