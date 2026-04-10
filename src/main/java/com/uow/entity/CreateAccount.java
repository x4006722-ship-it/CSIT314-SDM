package com.uow.entity;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.uow.util.DBUtils;

public class CreateAccount {
    /**
     * Entity for inserting a new row into user_account.
     * Column names are detected via metadata to tolerate schema variations.
     */
    private final String username;
    private final String password;
    private final String profileId;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String accountStatus;

    public CreateAccount(String username,
                         String password,
                         String profileId,
                         String fullName,
                         String email,
                         String phone,
                         String accountStatus) {
        this.username = username;
        this.password = password;
        this.profileId = profileId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.accountStatus = accountStatus;
    }

    public String saveToAccountDatabase() {
        String table = "user_account";

        try (Connection conn = DBUtils.getConnection()) {
            Set<String> cols = getLowercaseColumns(conn, table);

            List<String> insertCols = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            insertCols.add("username");
            values.add(username);

            insertCols.add("password");
            values.add(password);

            insertCols.add("profile_id");
            values.add(parseProfileId(profileId));

            if (cols.contains("a_status")) {
                insertCols.add("a_status");
                values.add(accountStatus != null ? accountStatus : "Active");
            }

            addIfPresent(cols, insertCols, values, fullName, "full_name", "fullname", "fullName", "full_name_text", "name");
            addIfPresent(cols, insertCols, values, email, "email", "e_mail", "user_email", "email_address");
            addIfPresent(cols, insertCols, values, phone, "phone", "phone_number", "phoneNo", "phone_no", "contact_number");

            String placeholders = String.join(", ", insertCols.stream().map(c -> "?").toList());
            String sql = "INSERT INTO " + table + " (" + String.join(", ", insertCols) + ") VALUES (" + placeholders + ")";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < values.size(); i++) {
                    Object v = values.get(i);
                    if (v instanceof Integer) {
                        pstmt.setInt(i + 1, (Integer) v);
                    } else {
                        pstmt.setString(i + 1, v != null ? String.valueOf(v) : null);
                    }
                }

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) return null;
                return "Insert failed (0 rows affected).";
            }
        } catch (SQLException e) {
            System.err.println("Database save failed! Reason: " + e.getMessage());
            e.printStackTrace();
            return e.getMessage() != null ? e.getMessage() : "Unknown database error";
        }
    }

    private static int parseProfileId(String profileId) throws SQLException {
        try {
            return Integer.parseInt(profileId);
        } catch (NumberFormatException e) {
            throw new SQLException("Invalid profileId: " + profileId, e);
        }
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

    private static void addIfPresent(Set<String> cols,
                                     List<String> insertCols,
                                     List<Object> values,
                                     String rawValue,
                                     String... candidates) {
        if (rawValue == null) return;
        String v = rawValue.trim();
        if (v.isEmpty()) return;

        for (String c : candidates) {
            if (c != null && cols.contains(c.toLowerCase()) && !insertCols.contains(c)) {
                insertCols.add(c);
                values.add(v);
                return;
            }
        }
    }
}

