package com.uow.entity;

import com.uow.util.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SuspendAccount {

    private static final String USER_ACCOUNT_TABLE = "user_account";
    private static final String USER_PROFILE_TABLE = "user_profile";
    /** Used by boundary to map to HTTP 403 — do not change without updating UserAdminSuspendAccount. */
    public static final String CANNOT_SUSPEND_USER_ADMIN = "CANNOT_SUSPEND_USER_ADMIN";
    /** User-facing error; also used by UpdateAccount / account boundaries. */
    public static final String MSG_CANNOT_SUSPEND_ACCOUNT = "User Admin accounts cannot be suspended.";

    private static boolean isUserAdminRole(String role) {
        return role != null && "User Admin".equalsIgnoreCase(role.trim());
    }

    /**
     * Toggles a_status between Active and Suspended for a given user_id.
     * User Admin accounts cannot be suspended (Active → Suspended); reactivation is still allowed.
     *
     * @return the new status, or null if user_id does not exist
     * @throws SQLException with {@link #CANNOT_SUSPEND_USER_ADMIN} when suspend is blocked for User Admin
     */
    public String toggleAccountStatus(int userId) throws SQLException {
        if (userId <= 0) throw new SQLException("Invalid userId.");

        String selectRoleSql = """
                SELECT ua.a_status, up.role
                FROM %s ua
                JOIN %s up ON ua.profile_id = up.profile_id
                WHERE ua.user_id = ?
                """.formatted(USER_ACCOUNT_TABLE, USER_PROFILE_TABLE);

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectRoleSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String currentStatus = rs.getString("a_status");
                String role = rs.getString("role");
                boolean wouldSuspend = currentStatus != null && "active".equalsIgnoreCase(currentStatus.trim());
                if (wouldSuspend && isUserAdminRole(role)) {
                    throw new SQLException(CANNOT_SUSPEND_USER_ADMIN);
                }
            }
        }

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

