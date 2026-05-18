package com.uow.logout;

import org.junit.Before;
import org.junit.Test;
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
    public void test_Logout_succeeds_with_valid_session() {
        boolean result = controller.logout(mockSession);
        assertTrue("Should return true for a valid session", result);
        assertTrue("Session should be invalidated", mockSession.isInvalidated());
    }

    @Test
    public void test_Logout_fails_when_session_is_null() {
        assertFalse("Should return false when session is null", controller.logout(null));
    }
}
