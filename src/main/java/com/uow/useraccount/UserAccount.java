package com.uow.useraccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uow.util.DBUtils;

public class UserAccount {

    // Full duplicate check across username, email, and phone
    public boolean isDuplicateAccount(String username, String email, String phone, int excludeUserId) {
        String sql = "SELECT 1 FROM user_account WHERE (LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?) OR phone_number = ?) AND user_id != ? LIMIT 1";
        try (Connection c = DBUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true = duplicate exists
            }
        } catch (SQLException e) {
            return false;
        }
    }

    //Create Account
    public boolean saveCreateAccount(Object newAccountData) {
        if (!(newAccountData instanceof Map<?, ?> data)) {
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO user_account (username,password,full_name,email,phone_number,a_status,profile_id) VALUES (?,?,?,?,?,?,?)")) {
            ps.setString(1, readText(data, "username"));
            ps.setString(2, readText(data, "password"));
            ps.setString(3, readText(data, "fullName"));
            ps.setString(4, readText(data, "email"));
            ps.setString(5, readText(data, "phoneNumber"));
            ps.setString(6, readText(data, "accountStatus"));
            ps.setInt(7, readInt(data, "profileId"));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            // Log SQL error for easier debugging
            System.err.println("[Create Account SQL Error]: " + e.getMessage());
            return false;
        }
    }

    //View Account
    public Object getViewAccount(int userId) {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT ua.user_id,ua.username,ua.full_name,ua.email,ua.phone_number,ua.password,ua.a_status,ua.profile_id,up.role AS roleName " +
                             "FROM user_account ua JOIN user_profile up ON ua.profile_id=up.profile_id WHERE ua.user_id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Map<String, Object> row = new HashMap<>();
                row.put("user_id", rs.getInt("user_id"));
                row.put("username", blankToEmpty(rs.getString("username")));
                row.put("full_name", blankToEmpty(rs.getString("full_name")));
                row.put("email", blankToEmpty(rs.getString("email")));
                row.put("phone_number", blankToEmpty(rs.getString("phone_number")));
                row.put("password", blankToEmpty(rs.getString("password")));
                row.put("a_status", blankToEmpty(rs.getString("a_status")));
                row.put("profile_id", rs.getInt("profile_id"));
                row.put("roleName", blankToEmpty(rs.getString("roleName")));
                return row;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /** Used when session has username but userId is missing or unparsable (e.g. "My Account"). */
    public int findUserIdByUsername(String username) {
        if (username == null || username.isBlank()) {
            return 0;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT user_id FROM user_account WHERE TRIM(LOWER(username)) = TRIM(LOWER(?)) LIMIT 1")) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    private static String blankToEmpty(String s) {
        return s == null ? "" : s;
    }

    //Update Account
    public boolean saveUpdateAccount(Object updatedAccountData) {
        if (!(updatedAccountData instanceof Map<?, ?> data)) {
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_account SET username=?, full_name=?, email=?, phone_number=?, password=?, a_status=?, profile_id=? WHERE user_id=?")) {
            ps.setString(1, readText(data, "username"));
            ps.setString(2, readText(data, "fullName"));
            ps.setString(3, readText(data, "email"));
            ps.setString(4, readText(data, "phoneNumber"));
            ps.setString(5, readText(data, "password"));
            ps.setString(6, readText(data, "accountStatus"));
            ps.setInt(7, readInt(data, "profileId"));
            ps.setInt(8, readInt(data, "userId"));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[Update Account SQL Error]: " + e.getMessage());
            return false;
        }
    }

    //Suspend Account
    public boolean saveSuspendAccount(int targetUserId, int currentUserId) {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_account SET a_status = CASE WHEN LOWER(TRIM(a_status))='suspended' THEN 'Active' ELSE 'Suspended' END WHERE user_id=?")) {
            ps.setInt(1, targetUserId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    //Search Account
    public Object getSearchAccount(Object searchData) {
        String username = "";
        String fullName = "";
        String email = "";
        String phoneNumber = "";
        String status = "";
        int profileId = 0;

        if (searchData instanceof Map<?, ?> data) {
            username = readText(data, "username");
            fullName = readText(data, "fullName");
            email = readText(data, "email");
            phoneNumber = readText(data, "phoneNumber");
            status = readText(data, "status");
            profileId = readInt(data, "profileID");
        }

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT ua.user_id,ua.username,ua.full_name,ua.email,ua.phone_number,ua.a_status,ua.profile_id,up.role AS roleName " +
                             "FROM user_account ua JOIN user_profile up ON ua.profile_id=up.profile_id " +
                             "WHERE (? = '' OR ua.username LIKE ?) " +
                             "AND (? = '' OR ua.full_name LIKE ?) " +
                             "AND (? = '' OR ua.email LIKE ?) " +
                             "AND (? = '' OR ua.phone_number LIKE ?) " +
                             "AND (? = '' OR ua.a_status = ?) " +
                             "AND (? = 0 OR ua.profile_id = ?)")) {
            ps.setString(1, username);
            ps.setString(2, "%" + username + "%");
            ps.setString(3, fullName);
            ps.setString(4, "%" + fullName + "%");
            ps.setString(5, email);
            ps.setString(6, "%" + email + "%");
            ps.setString(7, phoneNumber);
            ps.setString(8, "%" + phoneNumber + "%");
            ps.setString(9, status);
            ps.setString(10, status);
            ps.setInt(11, profileId);
            ps.setInt(12, profileId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> out = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("userId", rs.getInt("user_id"));
                    row.put("username", rs.getString("username"));
                    row.put("fullName", rs.getString("full_name"));
                    row.put("email", rs.getString("email"));
                    row.put("phoneNumber", rs.getString("phone_number"));
                    row.put("accountStatus", rs.getString("a_status"));
                    row.put("profileID", rs.getInt("profile_id"));
                    row.put("roleName", rs.getString("roleName"));
                    out.add(row);
                }
                return out;
            }
        } catch (Exception e) {
            return List.of();
        }
    }

    public Object getDoneeOptions() {
        List<Map<String, Object>> out = new ArrayList<>();
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT user_id, full_name FROM user_account WHERE profile_id = 3 ORDER BY full_name ASC, user_id ASC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("userId", rs.getInt("user_id"));
                    row.put("fullName", rs.getString("full_name"));
                    out.add(row);
                }
            }
            return out;
        } catch (Exception e) {
            return List.of();
        }
    }

    public Object getFundRaiserOptions() {
        List<Map<String, Object>> out = new ArrayList<>();
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT ua.user_id, ua.full_name "
                             + "FROM user_account ua "
                             + "JOIN user_profile up ON ua.profile_id = up.profile_id "
                             + "WHERE LOWER(TRIM(up.role)) = 'fund raiser' "
                             + "ORDER BY ua.full_name ASC, ua.user_id ASC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("userId", rs.getInt("user_id"));
                    row.put("fullName", rs.getString("full_name"));
                    out.add(row);
                }
            }
            return out;
        } catch (Exception e) {
            return List.of();
        }
    }

    private String readText(Map<?, ?> data, String key) {
        Object value = data.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int readInt(Map<?, ?> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null || String.valueOf(value).isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Object getDailyUserStats() {
        Map<String, Object> out = new HashMap<>();
        out.put("totalUserCount", 0);
        out.put("activeCount", 0);
        out.put("suspendedCount", 0);
        out.put("userByRole", new ArrayList<Map<String, Object>>());
        String createdColumn = "";
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_account'")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String col = rs.getString("COLUMN_NAME");
                    if ("created_at".equalsIgnoreCase(col) || "createdAt".equalsIgnoreCase(col)
                            || "user_createdAt".equalsIgnoreCase(col)) {
                        createdColumn = col;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            return out;
        }

        String whereClause = createdColumn.isBlank() ? "" : (" WHERE DATE(ua." + createdColumn + ") = CURDATE()");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) AS totalUserCount, "
                             + "SUM(CASE WHEN LOWER(TRIM(ua.a_status))='active' THEN 1 ELSE 0 END) AS activeCount, "
                             + "SUM(CASE WHEN LOWER(TRIM(ua.a_status))='suspended' THEN 1 ELSE 0 END) AS suspendedCount "
                             + "FROM user_account ua" + whereClause)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("totalUserCount", rs.getInt("totalUserCount"));
                    out.put("activeCount", rs.getInt("activeCount"));
                    out.put("suspendedCount", rs.getInt("suspendedCount"));
                }
            }
        } catch (SQLException e) {
            return out;
        }

        List<Map<String, Object>> byRole = new ArrayList<>();
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COALESCE(NULLIF(TRIM(up.role),''), 'Unknown') AS roleName, COUNT(*) AS userCount "
                             + "FROM user_account ua "
                             + "LEFT JOIN user_profile up ON ua.profile_id = up.profile_id"
                             + whereClause
                             + " GROUP BY COALESCE(NULLIF(TRIM(up.role),''), 'Unknown') "
                             + "ORDER BY userCount DESC, roleName ASC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("roleName", rs.getString("roleName"));
                    row.put("count", rs.getInt("userCount"));
                    byRole.add(row);
                }
            }
            out.put("userByRole", byRole);
        } catch (SQLException e) {
            return out;
        }
        return out;
    }

    public Object getWeeklyUserStats() {
        Map<String, Object> out = new HashMap<>();
        out.put("totalUserCount", 0);
        out.put("activeCount", 0);
        out.put("suspendedCount", 0);
        out.put("userByRole", new ArrayList<Map<String, Object>>());
        String createdColumn = "";
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_account'")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String col = rs.getString("COLUMN_NAME");
                    if ("created_at".equalsIgnoreCase(col) || "createdAt".equalsIgnoreCase(col)
                            || "user_createdAt".equalsIgnoreCase(col)) {
                        createdColumn = col;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            return out;
        }

        String whereClause = createdColumn.isBlank() ? "" : (" WHERE YEARWEEK(DATE(ua." + createdColumn + "), 1) = YEARWEEK(CURDATE(), 1)");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) AS totalUserCount, "
                             + "SUM(CASE WHEN LOWER(TRIM(ua.a_status))='active' THEN 1 ELSE 0 END) AS activeCount, "
                             + "SUM(CASE WHEN LOWER(TRIM(ua.a_status))='suspended' THEN 1 ELSE 0 END) AS suspendedCount "
                             + "FROM user_account ua" + whereClause)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("totalUserCount", rs.getInt("totalUserCount"));
                    out.put("activeCount", rs.getInt("activeCount"));
                    out.put("suspendedCount", rs.getInt("suspendedCount"));
                }
            }
        } catch (SQLException e) {
            return out;
        }

        List<Map<String, Object>> byRole = new ArrayList<>();
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COALESCE(NULLIF(TRIM(up.role),''), 'Unknown') AS roleName, COUNT(*) AS userCount "
                             + "FROM user_account ua "
                             + "LEFT JOIN user_profile up ON ua.profile_id = up.profile_id"
                             + whereClause
                             + " GROUP BY COALESCE(NULLIF(TRIM(up.role),''), 'Unknown') "
                             + "ORDER BY userCount DESC, roleName ASC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("roleName", rs.getString("roleName"));
                    row.put("count", rs.getInt("userCount"));
                    byRole.add(row);
                }
            }
            out.put("userByRole", byRole);
        } catch (SQLException e) {
            return out;
        }
        return out;
    }

    public Object getMonthlyUserStats() {
        Map<String, Object> out = new HashMap<>();
        out.put("totalUserCount", 0);
        out.put("activeCount", 0);
        out.put("suspendedCount", 0);
        out.put("userByRole", new ArrayList<Map<String, Object>>());
        String createdColumn = "";
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_account'")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String col = rs.getString("COLUMN_NAME");
                    if ("created_at".equalsIgnoreCase(col) || "createdAt".equalsIgnoreCase(col)
                            || "user_createdAt".equalsIgnoreCase(col)) {
                        createdColumn = col;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            return out;
        }

        String whereClause = createdColumn.isBlank() ? "" : (" WHERE YEAR(DATE(ua." + createdColumn + ")) = YEAR(CURDATE()) AND MONTH(DATE(ua." + createdColumn + ")) = MONTH(CURDATE())");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) AS totalUserCount, "
                             + "SUM(CASE WHEN LOWER(TRIM(ua.a_status))='active' THEN 1 ELSE 0 END) AS activeCount, "
                             + "SUM(CASE WHEN LOWER(TRIM(ua.a_status))='suspended' THEN 1 ELSE 0 END) AS suspendedCount "
                             + "FROM user_account ua" + whereClause)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("totalUserCount", rs.getInt("totalUserCount"));
                    out.put("activeCount", rs.getInt("activeCount"));
                    out.put("suspendedCount", rs.getInt("suspendedCount"));
                }
            }
        } catch (SQLException e) {
            return out;
        }

        List<Map<String, Object>> byRole = new ArrayList<>();
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COALESCE(NULLIF(TRIM(up.role),''), 'Unknown') AS roleName, COUNT(*) AS userCount "
                             + "FROM user_account ua "
                             + "LEFT JOIN user_profile up ON ua.profile_id = up.profile_id"
                             + whereClause
                             + " GROUP BY COALESCE(NULLIF(TRIM(up.role),''), 'Unknown') "
                             + "ORDER BY userCount DESC, roleName ASC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("roleName", rs.getString("roleName"));
                    row.put("count", rs.getInt("userCount"));
                    byRole.add(row);
                }
            }
            out.put("userByRole", byRole);
        } catch (SQLException e) {
            return out;
        }
        return out;
    }
}
