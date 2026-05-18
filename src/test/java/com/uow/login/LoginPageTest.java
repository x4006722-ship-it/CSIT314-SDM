package com.uow.login;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class LoginPageTest {

    private LoginPage loginPage;

    @Before
    public void setUp() {
        loginPage = new LoginPage();
    }

    @Test
    public void test_Login_fails_when_username_contains_illegal_characters() {
        Map<String, String> illegalInput = new HashMap<>();
        illegalInput.put("username", "user@123");
        illegalInput.put("password", "password");

        Object result = loginPage.userLogin(illegalInput, null);
        assertTrue(String.valueOf(result).contains("LoginPage.html"));
        assertTrue(String.valueOf(result).contains("Invalid+username+format"));
    }

    @Test
    public void test_Login_fails_when_fields_are_empty() {
        Map<String, String> emptyInput = new HashMap<>();
        emptyInput.put("username", "");
        emptyInput.put("password", "");

        Object result = loginPage.userLogin(emptyInput, null);
        assertTrue(String.valueOf(result).contains("LoginPage.html"));
        assertTrue(String.valueOf(result).contains("Empty+field+detected"));
    }

    @Test
    public void test_Login_fails_when_password_is_too_short() {
        Map<String, String> shortPasswordInput = new HashMap<>();
        shortPasswordInput.put("username", "admin");
        shortPasswordInput.put("password", "12");

        Object result = loginPage.userLogin(shortPasswordInput, null);
        assertTrue("Should redirect to login page", String.valueOf(result).contains("LoginPage.html"));
        assertTrue("Should contain length error message",
                String.valueOf(result).contains("Password+must+be+at+least+3+characters"));
    }

    @Test
    public void test_ShowLoginErrorMessage_encodes_and_redirects() {
        String result = loginPage.showLoginErrorMessage("Login failed.");
        assertTrue(result.contains("LoginPage.html"));
        assertTrue(result.contains("error="));
    }
}
