package com.uow.entity;

import com.uow.util.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SuspendAccount {

    private static final String USER_ACCOUNT_TABLE = "user_account";

    /**
     * Toggles a_status between Active and Suspended for a given user_id.
     *
     * @return the new status, or null if user_id does not exist
     */
    public String toggleAccountStatus(int userId) throws SQLException {
        if (userId <= 0) throw new SQLException("Invalid userId.");

        // Toggle between Active and Suspended. Any other value is treated as Active -> Suspended.
        String updateSql = """
                UPDATE %s
                SET a_status = CASE
                    WHEN LOWER(a_status) = 'active' THEN 'Suspended'
                    ELSE 'Active'
                END
                WHERE user_id = ?
                """.formatted(USER_ACCOUNT_TABLE);

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            if (rows <= 0) return null;
        }

        String selectSql = "SELECT a_status FROM " + USER_ACCOUNT_TABLE + " WHERE user_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("a_status");
                return null;
            }
        }
    }
}

