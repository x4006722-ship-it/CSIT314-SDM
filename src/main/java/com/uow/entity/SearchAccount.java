package com.uow.entity;

import com.uow.util.DBUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entity for searching user accounts with optional filters.
 * Returns lightweight rows for list/table display (paged).
 */
public class SearchAccount {
    private static final String USER_ACCOUNT_TABLE = "user_account";
    private static final String USER_PROFILE_TABLE = "user_profile";

    /**
     * Searches user accounts by keyword across username/fullname/email/phone,
     * and optionally filters by account status and role.
     */
    public List<Map<String, Object>> search(String query,
                                            String statusFilter,
                                            String roleFilter,
                                            int page,
                                            int pageSize) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            BuiltSearch built = buildSearch(conn, query, statusFilter, roleFilter);

            String sql = """
                    SELECT ua.user_id AS userId,
                           ua.username AS username,
                           %s AS fullName,
                           %s AS phoneNumber,
                           %s AS email,
                           ua.a_status AS accountStatus,
                           ua.profile_id AS profileId,
                           up.role AS roleName
                    FROM %s ua
                    JOIN %s up ON ua.profile_id = up.profile_id
                    %s
                    ORDER BY ua.user_id DESC
                    LIMIT ? OFFSET ?
                    """.formatted(
                    built.fullNameSelect,
                    built.phoneSelect,
                    built.emailSelect,
                    USER_ACCOUNT_TABLE,
                    USER_PROFILE_TABLE,
                    built.whereClause
            );

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int idx = 1;
                for (String p : built.params) {
                    ps.setString(idx++, p);
                }
                int safePage = Math.max(1, page);
                int safeSize = Math.max(1, pageSize);
                ps.setInt(idx++, safeSize);
                ps.setInt(idx, (safePage - 1) * safeSize);

                try (ResultSet rs = ps.executeQuery()) {
                    List<Map<String, Object>> out = new ArrayList<>();
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("userId", rs.getInt("userId"));
                        row.put("username", rs.getString("username"));
                        row.put("fullName", rs.getString("fullName"));
                        row.put("phoneNumber", rs.getString("phoneNumber"));
                        row.put("email", rs.getString("email"));
                        row.put("accountStatus", rs.getString("accountStatus"));
                        row.put("profileId", rs.getInt("profileId"));
                        row.put("roleName", rs.getString("roleName"));
                        out.add(row);
                    }
                    return out;
                }
            }
        }
    }

    /** Returns total count for the same search criteria. */
    public int count(String query, String statusFilter, String roleFilter) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            BuiltSearch built = buildSearch(conn, query, statusFilter, roleFilter);
            String sql = """
                    SELECT COUNT(*) AS total
                    FROM %s ua
                    JOIN %s up ON ua.profile_id = up.profile_id
                    %s
                    """.formatted(USER_ACCOUNT_TABLE, USER_PROFILE_TABLE, built.whereClause);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int idx = 1;
                for (String p : built.params) {
                    ps.setString(idx++, p);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("total");
                    return 0;
                }
            }
        }
    }

    private static class BuiltSearch {
        final String whereClause;
        final List<String> params;
        final String fullNameSelect;
        final String phoneSelect;
        final String emailSelect;

        BuiltSearch(String whereClause, List<String> params, String fullNameSelect, String phoneSelect, String emailSelect) {
            this.whereClause = whereClause;
            this.params = params;
            this.fullNameSelect = fullNameSelect;
            this.phoneSelect = phoneSelect;
            this.emailSelect = emailSelect;
        }
    }

    private static BuiltSearch buildSearch(Connection conn,
                                          String rawQuery,
                                          String rawStatusFilter,
                                          String rawRoleFilter) throws SQLException {
        String q = rawQuery == null ? "" : rawQuery.trim();
        String statusFilter = rawStatusFilter == null ? "" : rawStatusFilter.trim();
        String roleFilter = rawRoleFilter == null ? "" : rawRoleFilter.trim();

        Set<String> cols = getLowercaseColumns(conn, USER_ACCOUNT_TABLE);
        String fullNameCol = pickFirstColumn(cols, "full_name", "fullname", "fullName", "full_name_text", "name");
        String emailCol = pickFirstColumn(cols, "email", "e_mail", "user_email", "email_address");
        String phoneCol = pickFirstColumn(cols, "phone_number", "phone", "phoneNo", "phone_no", "contact_number");

        String fullNameSelect = fullNameCol != null ? "ua." + fullNameCol : "NULL";
        String phoneSelect = phoneCol != null ? "ua." + phoneCol : "NULL";
        String emailSelect = emailCol != null ? "ua." + emailCol : "NULL";

        List<String> whereParts = new ArrayList<>();
        List<String> params = new ArrayList<>();

        if (!statusFilter.isEmpty() && !"all".equalsIgnoreCase(statusFilter)) {
            whereParts.add("ua.a_status = ?");
            params.add(statusFilter);
        }

        if (!roleFilter.isEmpty() && !"all".equalsIgnoreCase(roleFilter)) {
            whereParts.add("up.role = ?");
            params.add(roleFilter);
        }

        if (!q.isEmpty()) {
            // Keyword search fields: username/fullname/email/phone
            List<String> ors = new ArrayList<>();
            String like = "%" + q + "%";

            ors.add("LOWER(ua.username) LIKE LOWER(?)");
            params.add(like);

            if (fullNameCol != null) {
                ors.add("LOWER(ua." + fullNameCol + ") LIKE LOWER(?)");
                params.add(like);
            }
            if (emailCol != null) {
                ors.add("LOWER(ua." + emailCol + ") LIKE LOWER(?)");
                params.add(like);
            }
            if (phoneCol != null) {
                ors.add("LOWER(ua." + phoneCol + ") LIKE LOWER(?)");
                params.add(like);
            }

            if (!ors.isEmpty()) {
                whereParts.add("(" + String.join(" OR ", ors) + ")");
            }
        }

        String where = whereParts.isEmpty() ? "" : "WHERE " + String.join(" AND ", whereParts);
        return new BuiltSearch(where, params, fullNameSelect, phoneSelect, emailSelect);
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

