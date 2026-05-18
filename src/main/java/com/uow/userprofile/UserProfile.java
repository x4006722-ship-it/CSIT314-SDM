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
    // Unique identifier for the profile
    private String profileId;
    // The role name (e.g., "User Admin", "Fund Raiser", "Donee")
    private String roleName;
    // Activation status ("Active" or "Suspended")
    private String status;

    
     //Constructs an empty UserProfile.
    
    public UserProfile() {}

    
    public UserProfile(String roleName, String status) {
        this.roleName = roleName;
        this.status = status;
    }

    
    public UserProfile(String profileId, String roleName, String status) {
        this.profileId = profileId;
        this.roleName = roleName;
        this.status = status;
    }

    public String getProfileId() { return profileId; } 
    public String getRoleName() { return roleName; }
    public String getStatus() { return status; }

    
    public boolean save() {
        String sql = "INSERT INTO user_profile (role, p_status) VALUES (?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.roleName);
            pstmt.setString(2, this.status);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
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
        } catch (SQLException e) { }
        return null;
    }

   
    public static List<UserProfile> findAll(String keyword, String status) {
        List<UserProfile> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT profile_id, role, p_status FROM user_profile WHERE 1=1");
        List<String> params = new ArrayList<>();

        // Add keyword filter if provided
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND LOWER(role) LIKE LOWER(?)");
            params.add("%" + keyword.trim() + "%");
        }
        // Add status filter if provided and not "all"
        if (status != null && !status.equals("all")) {
            sql.append(" AND p_status = ?");
            params.add(status);
        }

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
             
            // Set query parameters dynamically
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new UserProfile(rs.getString("profile_id"), rs.getString("role"), rs.getString("p_status")));
                }
            }
        } catch (SQLException e) { 
        }
        return list;
    }

    
    public boolean updateRoleName(String newRoleName) {
        String sql = "UPDATE user_profile SET role = ? WHERE profile_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newRoleName);
            pstmt.setString(2, this.profileId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            return false;
        }
    }

   
    public boolean updateStatus(String newStatus) {
        String sql = "UPDATE user_profile SET p_status = ? WHERE profile_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, this.profileId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            return false;
        }
    }

 
    public static boolean isDuplicateRole(String roleName, String excludeProfileId) {
        String sql = "SELECT COUNT(*) FROM user_profile WHERE LOWER(role) = LOWER(?)";
        if (excludeProfileId != null) {
            sql += " AND profile_id != ?";
        }
        
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, roleName.trim());
            if (excludeProfileId != null) {
                pstmt.setString(2, excludeProfileId);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // If count > 0, a duplicate role exists in the database
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) { 
            // Database exception handled silently
        }
        return false;
    }
}
