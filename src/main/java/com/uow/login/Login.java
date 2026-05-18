package com.uow.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.uow.util.DBUtils;

/**
 * Handles authentication and login verification logic.
 * 
 * Responsibilities:
 * - Validate user credentials against the database
 * - Retrieve user account and profile information on successful login
 * - Verify account and profile status
 * 
 * Usage: This class is called by LoginController during login processing to authenticate users.
 */
public class Login {

    /**
     * Verifies user login credentials by querying the database.
     * 
     * @param loginData A Map containing "username" and "password" fields
     * @return A Map containing user_id, account status, role, and profile status if login is successful;
     *         null if login fails or invalid data is provided
     */
    public Object verifyLogin(Object loginData) {
        if (!(loginData instanceof Map<?, ?> data)) {
            return null;
        }

        String username = readText(data.get("username"));
        String password = readText(data.get("password"));
        String sql = """
                SELECT ua.user_id, ua.a_status, up.role, up.p_status
                FROM user_account ua
                JOIN user_profile up ON ua.profile_id = up.profile_id
                WHERE ua.username = ? AND ua.password = ?
                """;

        try (Connection connection = DBUtils.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return Map.of(
                        "user_id", rs.getInt("user_id"),
                        "a_status", rs.getString("a_status"),
                        "role", rs.getString("role"),
                        "p_status", rs.getString("p_status")
                );
            }
        } catch (SQLException e) {
            // Database error occurred during login verification
            return null;
        }
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
}

