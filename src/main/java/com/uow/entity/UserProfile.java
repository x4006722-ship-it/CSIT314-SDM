package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.uow.util.DBUtils;

public class UserProfile {
    private String profileId;
    private String roleName;
    private String status;

    /** Simple profile/role record from user_profile. */
    public UserProfile(String profileId, String roleName, String status) {
        this.profileId = profileId;
        this.roleName = roleName;
        this.status = status;
    }

    /** Profile ID (stringified). */
    public String getProfileId() { return profileId; }
    /** Role display name. */
    public String getRoleName() { return roleName; }
    /** Profile status (Active/Suspended). */
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /** Inserts this profile into user_profile. */
    public boolean saveToPFDatabase() {
        String sql = "INSERT INTO user_profile (role, p_status) VALUES (?, ?)";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.roleName);
            pstmt.setString(2, this.status);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Database save failed! Reason: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<UserProfile> fetchAllProfiles() {
        String sql = "SELECT profile_id, role, p_status FROM user_profile ORDER BY profile_id";
        List<UserProfile> profiles = new ArrayList<>();

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String profileId = String.valueOf(rs.getInt("profile_id"));
                String roleName = rs.getString("role");
                String status = rs.getString("p_status");
                profiles.add(new UserProfile(profileId, roleName, status));
            }
        } catch (SQLException e) {
            System.err.println("Database fetch failed! Reason: " + e.getMessage());
            e.printStackTrace();
        }

        return profiles;
    }
}