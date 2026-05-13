package com.uow.logout;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class LogoutControllerTest {

    private LogoutController logoutController;

    @Before
    public void setUp() {
        logoutController = new LogoutController();
    }

    @Test
    public void testLogout_WithNullData_ReturnsFalse() {
        // 测试防线 1：传 null 进去
        boolean result = logoutController.logout(null);
        assertFalse("Result should be false when the session data is null", result);
    }

    @Test
    public void testLogout_WithInvalidType_ReturnsFalse() {
        // 测试防线 2：乱传一个 String 进去
        String invalidData = "This is definitely not a session object";
        boolean result = logoutController.logout(invalidData);
        assertFalse("Result should be false when data is an invalid type", result);
    }

    @Test
    public void testLogout_WithEmptyMap_ReturnsFalse() {
        // 测试防线 3：传一个没有 session 的空 Map 进去
        Map<String, Object> emptyMap = new HashMap<>();
        boolean result = logoutController.logout(emptyMap);
        assertFalse("Result should be false when map does not contain a session", result);
    }
}