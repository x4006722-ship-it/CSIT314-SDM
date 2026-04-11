package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.uow.util.DBUtils;

/**
 * 更改用户资料状态的 Entity 类
 * 责任：纯粹的数据访问层，负责执行 UPDATE 语句，不包含任何业务判断
 */
public class SuspendUserProfile {

    /**
     * 更新数据库中该 profile 的状态 (被 Control 层调用)
     * @param profileId 要修改的 profile ID
     * @param newStatus 新的状态值（"active" 或 "suspended"）
     * @return 更新是否成功
     */
    public static boolean updateStatusInDatabase(String profileId, String newStatus) {
        
        // 仅做最基础的数据格式校验
        if (profileId == null || profileId.trim().isEmpty() || 
            newStatus == null || newStatus.trim().isEmpty()) {
            System.err.println("[ENTITY] Invalid input: profileId or newStatus cannot be empty");
            return false;
        }

        String sql = "UPDATE user_profile SET p_status = ? WHERE profile_id = ?";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setString(2, profileId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("[ENTITY] Successfully updated profile " + profileId + " to status: " + newStatus);
                return true;
            } else {
                System.err.println("[ENTITY] No profile found with ID: " + profileId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("[ENTITY] Database update failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}