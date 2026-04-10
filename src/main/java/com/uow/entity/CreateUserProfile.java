package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.uow.util.DBUtils;

public class CreateUserProfile {

    public static final String MSG_ROLE_ALREADY_EXISTS = "Role already exists";

    /**
     * Duplicate detection: strip all Unicode whitespace, then lowercase.
     * "User Admin", "useradmin", " user  Admin " → same key.
     */
    public static String normalizeRoleKey(String roleName) {
        if (roleName == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(roleName.length());
        for (int i = 0; i < roleName.length(); i++) {
            char c = roleName.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString().toLowerCase(Locale.ROOT);
    }

    private String profileId;
    private String roleName;
    private String status;

    // Constructor
    public CreateUserProfile(String profileId, String roleName, String status) {
        this.profileId = profileId;
        this.roleName = roleName == null ? null : roleName.trim();
        this.status = status;
    }

    // Getters and Setters
    public String getProfileId() { return profileId; }
    public String getRoleName() { return roleName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * True if any row has the same {@link #normalizeRoleKey(String)} as this role name.
     */
    public boolean existsRoleNameIgnoreCase() {
        if (this.roleName == null || this.roleName.isEmpty()) {
            return false;
        }
        String target = normalizeRoleKey(this.roleName);
        if (target.isEmpty()) {
            return false;
        }
        String sql = "SELECT role FROM user_profile";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                if (target.equals(normalizeRoleKey(rs.getString("role")))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Duplicate role check failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

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
}
