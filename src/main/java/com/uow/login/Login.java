package com.uow.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.uow.util.DBUtils;

public class Login {

    private int user_id;
    private String role;

    public int getUserId() { return user_id; }
    public String getRole() { return role; }

    public boolean verifyLogin(String username, String password) {
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
                if (!rs.next()) return false;

                String aStatus = rs.getString("a_status");
                String pStatus = rs.getString("p_status");

                if (!"Active".equalsIgnoreCase(aStatus == null ? "" : aStatus.trim())) return false;
                if (!"Active".equalsIgnoreCase(pStatus == null ? "" : pStatus.trim())) return false;

                this.user_id = rs.getInt("user_id");
                this.role = rs.getString("role");
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}

