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
     * 更新数据库中该 profile 的角色名称
     * @return 更新是否成功
     */
    public boolean updateRoleNameInDatabase() {
        if (profileId == null || profileId.trim().isEmpty()
                || newRoleName == null || newRoleName.trim().isEmpty()) {
            System.err.println("[ENTITY] Invalid input: profileId or newRoleName cannot be empty");
            return false;
        }

        String existsSql = "SELECT COUNT(*) FROM user_profile WHERE profile_id = ?";
        String updateSql = "UPDATE user_profile SET role = ? WHERE profile_id = ?";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement existsStmt = conn.prepareStatement(existsSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            existsStmt.setString(1, profileId);
            try (ResultSet rs = existsStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.err.println("[ENTITY] No profile found with ID: " + profileId);
                    return false;
                }
            }

            updateStmt.setString(1, newRoleName);
            updateStmt.setString(2, profileId);
            int rowsAffected = updateStmt.executeUpdate();

            // MySQL may return 0 when value is unchanged.
            System.out.println("[ENTITY] Update executed for profile " + profileId
                    + ", rows affected: " + rowsAffected
                    + ", target role: " + newRoleName);
            return true;

        } catch (SQLException e) {
            System.err.println("[ENTITY] Database update failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
