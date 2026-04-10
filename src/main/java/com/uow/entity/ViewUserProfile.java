package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.uow.util.DBUtils;

public class ViewUserProfile {
    private String profileId;
    private String roleName;
    private String status;

    // Constructor
    public ViewUserProfile(String profileId) {
        this.profileId = profileId;
        this.roleName = null;
        this.status = null;
    }

    // Getters and Setters
    public String getProfileId() { return profileId; }
    public String getRoleName() { return roleName; }
    public String getStatus() { return status; }

    public ViewUserProfile getFromPFDatabase() {
        String sql = "SELECT profile_id, role, p_status FROM user_profile WHERE profile_id = ?";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.profileId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Fetch data from database and populate this object
                    this.profileId = rs.getString("profile_id");
                    this.roleName = rs.getString("role");
                    this.status = rs.getString("p_status");
                    System.out.println("Profile fetched successfully from database");
                    return this;
                } else {
                    System.err.println("Profile not found with ID: " + this.profileId);
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database fetch failed! Reason: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<ProfileDTO> getAllFromPFDatabase() {
        List<ProfileDTO> profiles = new ArrayList<>();
        String sql = "SELECT profile_id, role, p_status FROM user_profile";
        
        try (Connection conn = DBUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ProfileDTO profile = new ProfileDTO(
                    rs.getString("profile_id"),
                    rs.getString("role"),
                    rs.getString("p_status")
                );
                profiles.add(profile);
            }
            System.out.println("Successfully fetched " + profiles.size() + " profiles from database");
            
        } catch (SQLException e) {
            System.err.println("Database query failed! Reason: " + e.getMessage());
            e.printStackTrace();
        }
        
        return profiles;
    }

    // Inner class to represent profile data
    public static class ProfileDTO {
        public String profileId;
        public String roleName;
        public String status;
        
        public ProfileDTO(String profileId, String roleName, String status) {
            this.profileId = profileId;
            this.roleName = roleName;
            this.status = status;
        }
        
        public String getProfileId() { return profileId; }
        public String getRoleName() { return roleName; }
        public String getStatus() { return status; }
    }
}
