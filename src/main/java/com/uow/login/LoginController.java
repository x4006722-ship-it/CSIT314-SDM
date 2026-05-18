package com.uow.login;

import java.util.Map;

import org.springframework.stereotype.Controller;

/**
 * Controls the login business logic and user authentication flow.
 * 
 * Responsibilities:
 * - Delegate credential verification to the Login entity
 * - Check account and profile activation status
 * - Return authentication result with user ID and role
 * - Handle invalid credentials and inactive account scenarios
 * 
 * Usage: Called by LoginPage to authenticate users during the login process.
 */
@Controller
public class LoginController {

    // Entity responsible for verifying login credentials against the database
    private final Login loginEntity = new Login();

    /**
     * Authenticates a user by verifying credentials and checking account/profile status.
     * 
     * @param loginData A Map containing username and password
     * @return A Map with userId and role on success, or error message on failure
     */
    public Object login(Object loginData) {
        // Verify credentials against the database
        Object raw = loginEntity.verifyLogin(loginData);
        if (!(raw instanceof Map<?, ?> row)) {
            return Map.of("error", "Invalid credentials.");
        }

        // Check if both user account and profile are active
        String accountStatus = readText(row.get("a_status"));
        String profileStatus = readText(row.get("p_status"));
        if (!"Active".equalsIgnoreCase(accountStatus) || !"Active".equalsIgnoreCase(profileStatus)) {
            return Map.of("error", "Account or profile is not active.");
        }

        return Map.of(
                "userId", parseInt(row.get("user_id")),
                "role", readText(row.get("role"))
        );
    }

    /**
     * Safely converts an Object to a trimmed String.
     * 
     * @param value The object to convert (can be null)
     * @return The trimmed string value, or empty string if value is null
     */
    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    /**
     * Safely converts an Object to an integer.
     * 
     * @param value The object to convert (can be null, Number, or String)
     * @return The integer value, or 0 if conversion fails
     */
    private int parseInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(readText(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

