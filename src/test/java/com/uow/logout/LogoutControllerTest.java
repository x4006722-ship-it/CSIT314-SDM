package com.uow.logout;

import org.junit.Before;
import org.junit.Test;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class LogoutControllerTest {

    private LogoutController controller;
    private MockSession mockSession;

    @Before
    public void setUp() {
        controller = new LogoutController();
        mockSession = new MockSession();
    }

    @Test
    public void test_Logout_succeeds_when_passing_HttpSession_directly() {
        boolean result = controller.logout(mockSession);
        assertTrue("Should return true for direct session object", result);
        assertTrue("Session should be invalidated", mockSession.isInvalidated());
    }

    @Test
    public void test_Logout_succeeds_when_passing_session_wrapped_in_map() {
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("session", mockSession);

        boolean result = controller.logout(sessionMap);
        assertTrue("Should return true for session inside a Map", result);
        assertTrue("Session should be invalidated", mockSession.isInvalidated());
    }

    @Test
    public void test_Logout_fails_when_passing_invalid_object() {
        assertFalse("Should return false for random string input", controller.logout("NotASession"));
    }
}