package com.uow.logout;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LogoutPageTest {

    private LogoutPage logoutPage;

    @Before
    public void setUp() {
        logoutPage = new LogoutPage();
    }

    @Test
    public void test_User_logout_returns_redirect_to_login_page() {
        // 即使传入 null，也应该优雅地跳转回登录页
        String result = logoutPage.userLogout(null);
        assertEquals("redirect:/LoginPage.html", result);
    }

    @Test
    public void test_Show_login_page_returns_correct_path() {
        assertEquals("redirect:/LoginPage.html", logoutPage.showLoginPage());
    }
}
