package com.uow.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.uow.util.DBUtils;

public class Login {

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
            return null;
        }
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}

