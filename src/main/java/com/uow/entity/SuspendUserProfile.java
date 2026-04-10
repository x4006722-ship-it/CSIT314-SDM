package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.uow.util.DBUtils;

/**
 * 挂起用户资料的 Entity 类
 * 责任：直接执行数据库操作，修改 profile 的 p_status 字段
 */
public class SuspendUserProfile {

    private String profileId;
    private String newStatus;

    /**
     * 构造函数
     * @param profileId 要修改的 profile ID
     * @param newStatus 新的状态值（"active" 或 "suspended"）
     */
    public SuspendUserProfile(String profileId, String newStatus) {
        this.profileId = profileId;
        this.newStatus = newStatus;
    }

    /**
     * 更新数据库中该 profile 的状态
     * @return 更新是否成功
     */
    public boolean updateStatusInDatabase() {
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