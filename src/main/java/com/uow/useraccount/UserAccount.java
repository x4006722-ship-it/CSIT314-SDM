package com.uow.useraccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.uow.util.DBUtils;

public class UserAccount {

    public int userID;
    public String username;
    public String password;
    public String fullName;
    public String email;
    public String phoneNumber;
    public String status;
    public int profileID;
    public String lastErrorMessage = "";

    //Create Account
    public boolean saveCreateAccount() {
        lastErrorMessage = "";
        userID = 0;
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM user_account WHERE (username=? OR email=? OR phone_number=?) AND user_id<>?")) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, phoneNumber);
            ps.setInt(4, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || rs.getInt(1) != 0) {
                    lastErrorMessage = "Duplicate username, email, or phone number.";
                    return false;
                }
            }
        } catch (Exception e) {
            lastErrorMessage = "Duplicate username, email, or phone number.";
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO user_account (username,password,full_name,email,phone_number,a_status,profile_id) VALUES (?,?,?,?,?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, phoneNumber);
            ps.setString(6, status);
            ps.setInt(7, profileID);
            boolean ok = ps.executeUpdate() > 0;
            if (!ok) {
                lastErrorMessage = "Could not create account.";
            }
            return ok;
        } catch (Exception e) {
            lastErrorMessage = "Could not create account.";
            return false;
        }
    }

    //View Account
    public Object getViewAccount(int userID) {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT ua.user_id,ua.username,ua.full_name,ua.email,ua.phone_number,ua.password,ua.a_status,ua.profile_id,up.role AS roleName " +
                             "FROM user_account ua JOIN user_profile up ON ua.profile_id=up.profile_id WHERE ua.user_id=?")) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return java.util.Map.of(
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
    public boolean saveUpdateAccount(int userID) {
        lastErrorMessage = "";
        this.userID = userID;
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM user_account WHERE (username=? OR email=? OR phone_number=?) AND user_id<>?")) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, phoneNumber);
            ps.setInt(4, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || rs.getInt(1) != 0) {
                    lastErrorMessage = "Duplicate username, email, or phone number.";
                    return false;
                }
            }
        } catch (Exception e) {
            lastErrorMessage = "Duplicate username, email, or phone number.";
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_account SET username=?, full_name=?, email=?, phone_number=?, password=?, a_status=?, profile_id=? WHERE user_id=?")) {
            ps.setString(1, username);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, phoneNumber);
            ps.setString(5, password);
            ps.setString(6, status);
            ps.setInt(7, profileID);
            ps.setInt(8, userID);
            boolean ok = ps.executeUpdate() > 0;
            if (!ok) {
                lastErrorMessage = "Update failed.";
            }
            return ok;
        } catch (Exception e) {
            lastErrorMessage = "Update failed.";
            return false;
        }
    }

    //Suspend Account
    public boolean saveSuspendAccount(int targetUserId, int currentUserId) {
        lastErrorMessage = "";
        Object detail = getViewAccount(targetUserId);
        if (detail == null) {
            lastErrorMessage = "Account not found.";
            return false;
        }
        if (detail instanceof java.util.Map<?, ?> m) {
            Object role = m.get("roleName");
            if (role != null && "User Admin".equalsIgnoreCase(String.valueOf(role).trim())) {
                lastErrorMessage = "Cannot suspend User Admin account.";
                return false;
            }
        }

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_account SET a_status = CASE WHEN LOWER(TRIM(a_status))='suspended' THEN 'Active' ELSE 'Suspended' END WHERE user_id=?")) {
            ps.setInt(1, targetUserId);
            boolean ok = ps.executeUpdate() > 0;
            if (!ok) {
                lastErrorMessage = "Suspend failed.";
            }
            return ok;
        } catch (Exception e) {
            lastErrorMessage = "Suspend failed.";
            return false;
        }
    }

    //Search Account
    public Object getSearchAccount(String username, String fullName, String email, int phoneNumber, String status, int profileID) {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT ua.user_id,ua.username,ua.full_name,ua.email,ua.phone_number,ua.a_status,ua.profile_id,up.role AS roleName " +
                             "FROM user_account ua JOIN user_profile up ON ua.profile_id=up.profile_id " +
                             "WHERE (? = '' OR ua.username LIKE ?) " +
                             "AND (? = '' OR ua.full_name LIKE ?) " +
                             "AND (? = '' OR ua.email LIKE ?) " +
                             "AND (? = 0 OR ua.phone_number LIKE ?) " +
                             "AND (? = '' OR ua.a_status = ?) " +
                             "AND (? = 0 OR ua.profile_id = ?)")) {
            ps.setString(1, username);
            ps.setString(2, "%" + username + "%");
            ps.setString(3, fullName);
            ps.setString(4, "%" + fullName + "%");
            ps.setString(5, email);
            ps.setString(6, "%" + email + "%");
            ps.setInt(7, phoneNumber);
            ps.setString(8, "%" + (phoneNumber == 0 ? "" : String.valueOf(phoneNumber)) + "%");
            String st = status == null ? "" : status;
            ps.setString(9, st);
            ps.setString(10, st);
            ps.setInt(11, profileID);
            ps.setInt(12, profileID);
            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
                while (rs.next()) {
                    out.add(java.util.Map.of(
                            "userId", rs.getInt("user_id"),
                            "username", rs.getString("username"),
                            "fullName", rs.getString("full_name"),
                            "email", rs.getString("email"),
                            "phoneNumber", rs.getString("phone_number"),
                            "accountStatus", rs.getString("a_status"),
                            "profileID", rs.getInt("profile_id"),
                            "roleName", rs.getString("roleName")
                    ));
                }
                return out;
            }
        } catch (Exception e) {
            return java.util.List.of();
        }
    }
}
