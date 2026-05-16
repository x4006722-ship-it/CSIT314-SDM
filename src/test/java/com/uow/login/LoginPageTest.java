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
    public void test_Redirect_returns_correct_path_for_User_Admin() {
        assertEquals("redirect:/ManageProfile.html", loginPage.redirectPage("User Admin"));
    }

    @Test
    public void test_Redirect_returns_correct_path_for_Donee() {
        assertEquals("redirect:/DoneePage.html", loginPage.redirectPage("Donee"));
    }

    @Test
    public void test_Login_fails_when_username_contains_illegal_characters() {
        Map<String, String> illegalInput = new HashMap<>();
        illegalInput.put("username", "user@123"); // '@' is not allowed by the regex
        illegalInput.put("password", "password");

        Object result = loginPage.userLogin(illegalInput);
        // Should redirect to login page with error
        assertTrue(String.valueOf(result).contains("LoginPage.html"));
        assertTrue(String.valueOf(result).contains("Invalid+username+format"));
    }

    @Test
    public void test_Login_fails_when_fields_are_empty() {
        Map<String, String> emptyInput = new HashMap<>();
        emptyInput.put("username", "");
        emptyInput.put("password", "");

        Object result = loginPage.userLogin(emptyInput);
        assertTrue(String.valueOf(result).contains("Empty+field+detected"));
    }
}