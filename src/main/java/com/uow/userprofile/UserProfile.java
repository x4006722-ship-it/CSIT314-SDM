package com.uow.userprofile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import com.uow.util.DBUtils;

public class UserProfile {

    public int profileID;
    public String roleName;
    public String status;
    public String lastErrorMessage = "";

    // Getters for JSON serialization
    public int getProfileId() { return profileID; }
    public String getRoleName() { return roleName; }
    public String getStatus() { return status; }

    //Create Profile
    public boolean saveCreateProfile() {
        lastErrorMessage = "";
        try (Connection c = DBUtils.getConnection();
             PreparedStatement dup = c.prepareStatement(
                     "SELECT 1 FROM user_profile WHERE LOWER(TRIM(role)) = LOWER(TRIM(?)) LIMIT 1")) {
            dup.setString(1, roleName);
            try (ResultSet rs = dup.executeQuery()) {
                if (rs.next()) {
                    lastErrorMessage = "Role name already exists.";
                    return false;
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = "Failed to create profile.";
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO user_profile (role, p_status) VALUES (?, ?)")) {
            ps.setString(1, roleName);
            ps.setString(2, status);
            boolean ok = ps.executeUpdate() > 0;
            if (!ok) lastErrorMessage = "Failed to create profile.";
            return ok;
        } catch (SQLException e) {
            lastErrorMessage = "Failed to create profile.";
            return false;
        }
    }

    //View Profile
    public Object getViewProfile(int profileID) {
        lastErrorMessage = "";
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT profile_id, role, p_status FROM user_profile WHERE profile_id = ? LIMIT 1")) {
            ps.setInt(1, profileID);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Map<String, Object> obj = new LinkedHashMap<>();
                obj.put("profileId", rs.getInt("profile_id"));
                obj.put("roleName", rs.getString("role"));
                obj.put("status", rs.getString("p_status"));
                return obj;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    //Update Profile
    public boolean saveUpdateProfile(int profileID) {
        lastErrorMessage = "";
        this.profileID = profileID;
        try (Connection c = DBUtils.getConnection();
             PreparedStatement dup = c.prepareStatement(
                     "SELECT 1 FROM user_profile WHERE LOWER(TRIM(role)) = LOWER(TRIM(?)) AND profile_id <> ? LIMIT 1")) {
            dup.setString(1, roleName);
            dup.setInt(2, profileID);
            try (ResultSet rs = dup.executeQuery()) {
                if (rs.next()) {
                    lastErrorMessage = "Role name already exists.";
                    return false;
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = "Failed to update profile.";
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_profile SET role = ?, p_status = ? WHERE profile_id = ?")) {
            ps.setString(1, roleName);
            ps.setString(2, status);
            ps.setInt(3, profileID);
            boolean ok = ps.executeUpdate() > 0;
            if (!ok) lastErrorMessage = "Failed to update profile.";
            return ok;
        } catch (SQLException e) {
            lastErrorMessage = "Failed to update profile.";
            return false;
        }
    }

    //Suspend Profile (toggle Active ↔ Suspended)
    public boolean saveSuspendProfile(int profileID) {
        lastErrorMessage = "";
        this.profileID = profileID;
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_profile SET p_status = CASE WHEN LOWER(TRIM(p_status))='suspended' THEN 'Active' ELSE 'Suspended' END WHERE profile_id=?")) {
            ps.setInt(1, profileID);
            boolean ok = ps.executeUpdate() > 0;
            if (!ok) lastErrorMessage = "Failed to update profile status.";
            return ok;
        } catch (SQLException e) {
            lastErrorMessage = "Failed to update profile status.";
            return false;
        }
    }

    //Search Profile
    public Object getSearchProfile(String keyword, String status) {
        lastErrorMessage = "";
        String kw = keyword == null ? "" : keyword.trim();
        String st = status == null ? "" : status.trim();

        StringBuilder sql = new StringBuilder(
                "SELECT profile_id, role, p_status FROM user_profile WHERE 1=1 ");
        if (!kw.isEmpty()) sql.append("AND role LIKE ? ");
        if (!st.isEmpty() && !st.equalsIgnoreCase("all")) sql.append("AND p_status = ? ");
        sql.append("ORDER BY profile_id LIMIT 2000");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int idx = 1;
            if (!kw.isEmpty()) ps.setString(idx++, "%" + kw + "%");
            if (!st.isEmpty() && !st.equalsIgnoreCase("all")) ps.setString(idx++, st);

            List<Map<String, Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("profileId", rs.getInt("profile_id"));
                    row.put("roleName", rs.getString("role"));
                    row.put("status", rs.getString("p_status"));
                    out.add(row);
                }
            }
            return out;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
}
