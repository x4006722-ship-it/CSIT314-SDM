package com.uow.useraccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uow.util.DBUtils;

public class UserAccount {

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
                return Map.of(
                        "user_id", rs.getInt("user_id"),
                        "username", rs.getString("username"),
                        "full_name", rs.getString("full_name"),
                        "email", rs.getString("email"),
                        "phone_number", rs.getString("phone_number"),
                        "password", rs.getString("password"),
                        "a_status", rs.getString("a_status"),
                        "profile_id", rs.getInt("profile_id"),
                        "roleName", rs.getString("roleName")
                );
            }
        } catch (Exception e) {
            return null;
        }
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
}
