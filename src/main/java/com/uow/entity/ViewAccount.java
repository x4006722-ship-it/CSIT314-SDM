package com.uow.entity;

import com.uow.util.DBUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entity for reading user accounts.
 * Provides a paged "summary" list and a full detail record by user_id.
 */
public class ViewAccount {

    private static final String USER_ACCOUNT_TABLE = "user_account";
    private static final String USER_PROFILE_TABLE = "user_profile";

    public List<Map<String, Object>> fetchAccountSummaries(int page, int pageSize) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            Set<String> cols = getLowercaseColumns(conn, USER_ACCOUNT_TABLE);
            String fullNameCol = pickFirstColumn(cols, "full_name", "fullname", "fullName", "full_name_text", "name");
            String phoneCol = pickFirstColumn(cols, "phone_number", "phone", "phoneNo", "phone_no", "contact_number");

            String fullNameSelect = fullNameCol != null ? "ua." + fullNameCol + " AS fullName" : "NULL AS fullName";
            // Keep original column label to avoid driver alias quirks.
            String statusSelect = "ua.a_status";
            String phoneSelect = phoneCol != null ? "ua." + phoneCol + " AS phoneNumber" : "NULL AS phoneNumber";

            String sql = """
                    SELECT ua.user_id AS userId,
                           ua.username AS username,
                           %s,
                           %s,
                           %s,
                           ua.profile_id AS profileId,
                           up.role AS roleName
                    FROM %s ua
                    JOIN %s up ON ua.profile_id = up.profile_id
                    ORDER BY ua.user_id DESC
                    LIMIT ? OFFSET ?
                    """.formatted(fullNameSelect, phoneSelect, statusSelect, USER_ACCOUNT_TABLE, USER_PROFILE_TABLE);

            try (PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                int safePage = Math.max(1, page);
                int safeSize = Math.max(1, pageSize);
                ps.setInt(1, safeSize);
                ps.setInt(2, (safePage - 1) * safeSize);

                try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> out = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("userId", rs.getInt("userId"));
                    row.put("username", rs.getString("username"));
                    row.put("fullName", rs.getString("fullName"));
                    row.put("phoneNumber", rs.getString("phoneNumber"));
                    row.put("accountStatus", rs.getString("a_status"));
                    row.put("profileId", rs.getInt("profileId"));
                    row.put("roleName", rs.getString("roleName"));
                    out.add(row);
                }
                return out;
                }
            }
        }
    }

    public int fetchAccountCount() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM " + USER_ACCOUNT_TABLE;
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
            return 0;
        }
    }

    public Map<String, Object> fetchAccountDetail(int userId) throws SQLException {
        String sql = """
                SELECT ua.*, up.role AS roleName
                FROM %s ua
                JOIN %s up ON ua.profile_id = up.profile_id
                WHERE ua.user_id = ?
                """.formatted(USER_ACCOUNT_TABLE, USER_PROFILE_TABLE);

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Map<String, Object> out = new LinkedHashMap<>();
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String label = md.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    out.put(label, value);
                }
                return out;
            }
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

