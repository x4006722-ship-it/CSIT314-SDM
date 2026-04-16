package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.uow.util.DBUtils;

public class Login {

    public int user_id;
    public int profile_id;
    public String role;

    private String a_status;
    private String p_status;

    public boolean verifyCredentials(String username, String password) {
        String sql = """
                SELECT ua.user_id, ua.username, ua.password, ua.a_status, ua.profile_id, up.role, up.p_status
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
                    return false;
                }

                this.user_id = rs.getInt("user_id");
                this.profile_id = rs.getInt("profile_id");
                this.role = rs.getString("role");
                this.a_status = rs.getString("a_status");
                this.p_status = rs.getString("p_status");
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean checkAccountStatus() {
        return a_status != null && "Active".equalsIgnoreCase(a_status.trim());
    }

    public boolean checkProfileStatus() {
        return p_status != null && "Active".equalsIgnoreCase(p_status.trim());
    }
}

