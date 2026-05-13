package com.uow.login;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class LoginControllerTest {

    private LoginController loginController;

    @Before
    public void setUp() {
        loginController = new LoginController();
    }

    @Test
    public void testLogin_WithNullData_ReturnsErrorMap() {
        // Execution
        Object result = loginController.login(null);
        
        // Verification: It should gracefully return a Map containing an error message
        assertTrue("Result should be a Map", result instanceof Map);
        
        Map<?, ?> resultMap = (Map<?, ?>) result;
        assertTrue("Result map should contain an 'error' key", resultMap.containsKey("error"));
        assertEquals("Invalid credentials.", resultMap.get("error"));
    }

    @Test
    public void testLogin_WithInvalidCredentials_ReturnsErrorMap() {
        // Setup: Pass a map with fake credentials that definitely do not exist in the DB
        Map<String, String> fakeLogin = new HashMap<>();
        fakeLogin.put("username", "fake_user_999");
        fakeLogin.put("password", "wrong_password");

        // Execution
        Object result = loginController.login(fakeLogin);

        // Verification: The database query will fail, returning null, which the controller handles
        assertTrue("Result should be a Map", result instanceof Map);
        
        Map<?, ?> resultMap = (Map<?, ?>) result;
        assertEquals("Invalid credentials.", resultMap.get("error"));
    }
}