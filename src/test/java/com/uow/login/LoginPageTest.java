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
        assertTrue(String.valueOf(result).contains("LoginPage.html"));
        assertTrue(String.valueOf(result).contains("Empty+field+detected"));
    }

    // 【新增】测试密码长度小于3位的拦截逻辑
    @Test
    public void test_Login_fails_when_password_is_too_short() {
        Map<String, String> shortPasswordInput = new HashMap<>();
        shortPasswordInput.put("username", "admin");
        shortPasswordInput.put("password", "12"); // 只有两位

        Object result = loginPage.userLogin(shortPasswordInput);
        
        // 验证是否重定向回登录页
        assertTrue("Should redirect to login page", String.valueOf(result).contains("LoginPage.html"));
        // 验证是否带有对应的错误提示 (空格会被 URLEncoder 转换为 '+')
        assertTrue("Should contain length error message", 
                   String.valueOf(result).contains("Password+must+be+at+least+3+characters"));
    }
}