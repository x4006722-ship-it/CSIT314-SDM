package com.uow.userprofile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.uow.util.DBUtils;

public class UserProfile {
    private String profileId;
    private String roleName;
    private String status;

    // Default constructor required by frameworks
    public UserProfile() {}

    // Constructor for creating new profiles
    public UserProfile(String roleName, String status) {
        this.roleName = roleName;
        this.status = status;
    }

    // Full constructor for retrieving records from the database
    public UserProfile(String profileId, String roleName, String status) {
        this.profileId = profileId;
        this.roleName = roleName;
        this.status = status;
    }

    // Getters for Spring Boot JSON serialization
    public String getProfileId() { return profileId; } 
    public String getRoleName() { return roleName; }
    public String getStatus() { return status; }

    // ==========================================
    // Database Operations (High Cohesion: All DB logic stays here)
    // ==========================================

    public Boolean save() {
        String sql = "INSERT INTO user_profile (role, p_status) VALUES (?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.roleName);
            pstmt.setString(2, this.status);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            System.err.println("[ENTITY ERROR] Database insert failed: " + e.getMessage());
            return false; 
        }
    }

    public static UserProfile findByID(String profileID) {
        String sql = "SELECT profile_id, role, p_status FROM user_profile WHERE profile_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserProfile(rs.getString("profile_id"), rs.getString("role"), rs.getString("p_status"));
                }
            }
        } catch (SQLException e) { 
            System.err.println("[ENTITY ERROR] Database query failed: " + e.getMessage());
        }
        return null;
    }

    public static List<UserProfile> findAll(String keyword, String status) {
        List<UserProfile> list = new ArrayList<>();
        String sql = "SELECT profile_id, role, p_status FROM user_profile WHERE 1=1";
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND role LIKE '%" + keyword.trim() + "%'";
        }
        if (status != null && !status.equals("all")) {
            sql += " AND p_status = '" + status + "'";
        }

        try (Connection conn = DBUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new UserProfile(rs.getString("profile_id"), rs.getString("role"), rs.getString("p_status")));
            }
        } catch (SQLException e) { 
            System.err.println("[ENTITY ERROR] Database list retrieval failed: " + e.getMessage());
        }
        return list;
    }

    public void updateRoleName(String newRoleName) {
        String sql = "UPDATE user_profile SET role = ? WHERE profile_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newRoleName);
            pstmt.setString(2, this.profileId);
            pstmt.executeUpdate();
        } catch (SQLException e) { 
            System.err.println("[ENTITY ERROR] Failed to update role name: " + e.getMessage());
        }
    }

    public void updateStatus(String newStatus) {
        String sql = "UPDATE user_profile SET p_status = ? WHERE profile_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, this.profileId);
            pstmt.executeUpdate();
        } catch (SQLException e) { 
            System.err.println("[ENTITY ERROR] Failed to update status: " + e.getMessage());
        }
    }
}
