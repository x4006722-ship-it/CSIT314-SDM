package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.uow.util.DBUtils;

/**
 * 更新用户资料名称的 Entity 类
 * 责任：纯粹的数据操作层，只负责执行 UPDATE 语句，不包含业务校验
 */
public class UpdateUserProfileName {

    /**
     * 更新数据库中该 profile 的角色名称
     * @param profileId 要更新的 profile ID
     * @param newRoleName 新的角色名称
     * @return 更新是否成功（如果找不到 ID 返回 false）
     */
    public static boolean updateRoleNameInDatabase(String profileId, String newRoleName) {
        String sql = "UPDATE user_profile SET role = ? WHERE profile_id = ?";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRoleName.trim());
            pstmt.setString(2, profileId.trim());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("[ENTITY] 成功将 profile " + profileId + " 的 role 更新为: " + newRoleName);
                return true;
            } else {
                System.err.println("[ENTITY] 找不到对应的 profile ID: " + profileId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("[ENTITY] 数据库更新失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
