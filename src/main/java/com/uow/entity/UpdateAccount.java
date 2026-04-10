package com.uow.entity;

import com.uow.util.DBUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdateAccount {

    private static final String USER_ACCOUNT_TABLE = "user_account";
    private static final String USER_PROFILE_TABLE = "user_profile";

    private static boolean isUserAdminRole(String role) {
        return role != null && "User Admin".equalsIgnoreCase(role.trim());
    }

    private static boolean fieldMapRequestsAccountSuspend(Map<String, Object> fields) {
        if (fields == null) return false;
        for (Map.Entry<String, Object> e : fields.entrySet()) {
            String key = e.getKey();
            if (key == null) continue;
            String k = key.trim();
            if (k.isEmpty()) continue;
            if (!"a_status".equalsIgnoreCase(k) && !"account_status".equalsIgnoreCase(k)) continue;
            Object raw = e.getValue();
            if (raw == null) continue;
            String value = String.valueOf(raw).trim();
            if (value.isEmpty()) continue;
            if ("suspended".equalsIgnoreCase(value)) return true;
        }
        return false;
    }

    /**
     * @return error message if this user is User Admin and must not be suspended; null if allowed or not applicable
     */
    private String blockSuspendIfUserAdminAccount(int userId) throws SQLException {
        String sql = """
                SELECT up.role
                FROM %s ua
                INNER JOIN %s up ON ua.profile_id = up.profile_id
                WHERE ua.user_id = ?
                """.formatted(USER_ACCOUNT_TABLE, USER_PROFILE_TABLE);
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                if (isUserAdminRole(rs.getString("role"))) {
                    return SuspendAccount.MSG_CANNOT_SUSPEND_ACCOUNT;
                }
            }
        }
        return null;
    }

    /**
     * Updates editable fields for a user_account row.
     * user_id is never updated; a_status/profile_id are validated.
     *
     * @return null on success; otherwise an error message
     */
    public String updateAccount(int userId, Map<String, Object> fields) {
        if (userId <= 0) return "Invalid userId.";
        if (fields == null || fields.isEmpty()) return "No fields to update.";

        try {
            if (fieldMapRequestsAccountSuspend(fields)) {
                String block = blockSuspendIfUserAdminAccount(userId);
                if (block != null) return block;
            }
        } catch (SQLException e) {
            System.err.println("Suspend guard query failed: " + e.getMessage());
            e.printStackTrace();
            return e.getMessage() != null ? e.getMessage() : "Unknown database error";
        }

        try (Connection conn = DBUtils.getConnection()) {
            Set<String> cols = getLowercaseColumns(conn, USER_ACCOUNT_TABLE);

            Map<String, String> keyToColumn = new HashMap<>();
            keyToColumn.put("username", "username");
            keyToColumn.put("password", "password");
            keyToColumn.put("profile_id", "profile_id");
            keyToColumn.put("a_status", "a_status");
            keyToColumn.put("account_status", "a_status");

            String fullNameCol = pickFirstColumn(cols, "full_name", "fullname", "fullName", "full_name_text", "name");
            if (fullNameCol != null) keyToColumn.put("full_name", fullNameCol);

            String emailCol = pickFirstColumn(cols, "email", "e_mail", "user_email", "email_address");
            if (emailCol != null) keyToColumn.put("email", emailCol);

            String phoneCol = pickFirstColumn(cols, "phone_number", "phone", "phoneNo", "phone_no", "contact_number");
            if (phoneCol != null) keyToColumn.put("phone_number", phoneCol);

            Set<String> forbidden = Set.of("user_id");

            List<String> setClauses = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            for (Map.Entry<String, Object> e : fields.entrySet()) {
                String key = e.getKey();
                if (key == null) continue;
                String k = key.trim();
                if (k.isEmpty()) continue;
                if (forbidden.contains(k)) continue;

                String column = keyToColumn.get(k);
                if (column == null) continue;
                if (!cols.contains(column.toLowerCase())) continue;

                Object raw = e.getValue();
                if (raw == null) continue;
                String value = String.valueOf(raw).trim();
                if (value.isEmpty()) continue;

                if ("profile_id".equals(column)) {
                    int pid;
                    try {
                        pid = Integer.parseInt(value);
                    } catch (NumberFormatException ex) {
                        return "Invalid profile_id.";
                    }
                    setClauses.add(column + " = ?");
                    params.add(pid);
                } else if ("a_status".equalsIgnoreCase(column)) {
                    String normalized = value.trim();
                    if (!normalized.equalsIgnoreCase("Active") && !normalized.equalsIgnoreCase("Suspended")) {
                        return "Invalid account status (must be Active or Suspended).";
                    }
                    setClauses.add(column + " = ?");
                    params.add(normalized.substring(0, 1).toUpperCase() + normalized.substring(1).toLowerCase());
                } else {
                    setClauses.add(column + " = ?");
                    params.add(value);
                }
            }

            if (setClauses.isEmpty()) return "No valid fields to update.";

            String sql = "UPDATE " + USER_ACCOUNT_TABLE + " SET " + String.join(", ", setClauses) + " WHERE user_id = ?";
            params.add(userId);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    Object p = params.get(i);
                    if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                    else ps.setString(i + 1, String.valueOf(p));
                }

                int rows = ps.executeUpdate();
                if (rows <= 0) return "Update failed (0 rows affected).";
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Database update failed! Reason: " + e.getMessage());
            e.printStackTrace();
            return e.getMessage() != null ? e.getMessage() : "Unknown database error";
        }
    }

    private static String pickFirstColumn(Set<String> lowercaseCols, String... candidates) {
        for (String c : candidates) {
            if (c != null && lowercaseCols.contains(c.toLowerCase())) return c;
        }
        return null;
    }

    private static Set<String> getLowercaseColumns(Connection conn, String table) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        Set<String> cols = new HashSet<>();
        try (ResultSet rs = meta.getColumns(conn.getCatalog(), null, table, null)) {
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                if (name != null) cols.add(name.toLowerCase());
            }
        }
        if (!cols.isEmpty()) return cols;

        try (ResultSet rs = meta.getColumns(conn.getCatalog(), null, table.toUpperCase(), null)) {
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                if (name != null) cols.add(name.toLowerCase());
            }
        }
        return cols;
    }
}

