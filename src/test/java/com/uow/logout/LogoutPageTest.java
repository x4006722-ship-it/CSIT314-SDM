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
    public void testUserLogout_WithNullSession_ReturnsRedirect() {
        // 直接传 null，测试它能不能安全地把用户踢回登录页
        String viewName = logoutPage.userLogout(null);
        
        assertEquals("Should redirect to login page even if session is null", "redirect:/LoginPage.html", viewName);
    }
}
