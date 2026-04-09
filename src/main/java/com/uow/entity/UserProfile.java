package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.uow.util.DBUtils;

public class UserProfile {
    private String profileId;
    private String roleName;
    private String status;

    // Constructor
    public UserProfile(String profileId, String roleName, String status) {
        this.profileId = profileId;
        this.roleName = roleName;
        this.status = status;
    }

    // Getters and Setters
    public String getProfileId() { return profileId; }
    public String getRoleName() { return roleName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean saveToPFDatabase() {
        String sql = "INSERT INTO user_profile (role, p_status) VALUES (?, ?)";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Insert this object's variables (this.roleName, this.status) into the SQL query
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
}