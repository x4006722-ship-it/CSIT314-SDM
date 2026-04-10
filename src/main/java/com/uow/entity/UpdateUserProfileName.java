package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.uow.util.DBUtils;

/**
 * 更新用户资料名称的 Entity 类
 * 责任：直接执行数据库操作，修改 role 字段
 */
public class UpdateUserProfileName {

    private String profileId;
    private String newRoleName;

    /**
     * 构造函数
     * @param profileId 要更新的 profile ID
     * @param newRoleName 新的 role name
     */
    public UpdateUserProfileName(String profileId, String newRoleName) {
        this.profileId = profileId;
        this.newRoleName = newRoleName;
    }

    /**
     * 更新数据库中该 profile 的角色名称（与其它 profile 的规范化名不可重复，见 {@link CreateUserProfile#normalizeRoleKey}）。
     * @return null 成功；否则为错误信息（含 {@link CreateUserProfile#MSG_ROLE_ALREADY_EXISTS}）
     */
    public String updateRoleNameInDatabase() {
        if (profileId == null || profileId.trim().isEmpty()
                || newRoleName == null || newRoleName.trim().isEmpty()) {
            System.err.println("[ENTITY] Invalid input: profileId or newRoleName cannot be empty");
            return "Invalid profile or role name.";
        }

        final String trimmedPid = profileId.trim();
        final String trimmedNew = newRoleName.trim();
        final String newKey = CreateUserProfile.normalizeRoleKey(trimmedNew);

        String existsSql = "SELECT COUNT(*) FROM user_profile WHERE profile_id = ?";
        String scanSql = "SELECT profile_id, role FROM user_profile";
        String updateSql = "UPDATE user_profile SET role = ? WHERE profile_id = ?";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement existsStmt = conn.prepareStatement(existsSql);
             PreparedStatement scanStmt = conn.prepareStatement(scanSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            existsStmt.setString(1, trimmedPid);
            try (ResultSet rs = existsStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.err.println("[ENTITY] No profile found with ID: " + profileId);
                    return "Profile not found.";
                }
            }

            try (ResultSet rs = scanStmt.executeQuery()) {
                while (rs.next()) {
                    String otherId = String.valueOf(rs.getObject("profile_id"));
                    if (otherId.equals(trimmedPid)) {
                        continue;
                    }
                    if (newKey.equals(CreateUserProfile.normalizeRoleKey(rs.getString("role")))) {
                        return CreateUserProfile.MSG_ROLE_ALREADY_EXISTS;
                    }
                }
            }

            updateStmt.setString(1, trimmedNew);
            updateStmt.setString(2, trimmedPid);
            int rowsAffected = updateStmt.executeUpdate();

            System.out.println("[ENTITY] Update executed for profile " + profileId
                    + ", rows affected: " + rowsAffected
                    + ", target role: " + trimmedNew);
            return null;

        } catch (SQLException e) {
            System.err.println("[ENTITY] Database update failed: " + e.getMessage());
            e.printStackTrace();
            return e.getMessage() != null ? e.getMessage() : "Database error";
        }
    }
}
