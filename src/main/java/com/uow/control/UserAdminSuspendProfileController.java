package com.uow.control;

import com.uow.entity.SuspendUserProfile;
import com.uow.util.DBUtils; // 引入 DBUtils
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 挂起用户资料的 Control 层
 * 责任：协调 Entity 执行业务逻辑，处理状态转换规则，并负责必要的数据库校验查询
 */
@Service
public class UserAdminSuspendProfileController {

    public enum SuspendProfileOutcome {
        SUCCESS,
        NOT_FOUND,
        USER_ADMIN_FORBIDDEN
    }

    private static boolean isUserAdminRole(String role) {
        return role != null && "User Admin".equalsIgnoreCase(role.trim());
    }

    /**
     * 挂起（暂停）一个 profile（User Admin 不可挂起）
     * @param profileId 要挂起的 profile ID
     */
    public SuspendProfileOutcome suspendProfileWithOutcome(String profileId) {
        System.out.println("[CONTROL] Suspending profile: " + profileId);

        // 1. 业务校验：直接从数据库查询该 profileId 对应的角色 (不再使用 ViewUserProfile)
        String role = fetchRoleFromDatabase(profileId);
        
        if (role == null) {
            System.err.println("[CONTROL] Failed to suspend profile: not found");
            return SuspendProfileOutcome.NOT_FOUND;
        }

        // 2. 业务校验：User Admin 保护机制
        if (isUserAdminRole(role)) {
            System.err.println("[CONTROL] Cannot suspend User Admin profile");
            return SuspendProfileOutcome.USER_ADMIN_FORBIDDEN;
        }

        // 3. 校验通过，直接指挥 Entity 层执行更新
        boolean success = SuspendUserProfile.updateStatusInDatabase(profileId, "suspended");

        if (success) {
            System.out.println("[CONTROL] Profile suspended successfully");
            return SuspendProfileOutcome.SUCCESS;
        }
        System.err.println("[CONTROL] Failed to suspend profile");
        return SuspendProfileOutcome.NOT_FOUND;
    }

    /**
     * 重新激活一个 profile
     * @param profileId 要激活的 profile ID
     * @return 操作是否成功
     */
    public boolean reactivateProfile(String profileId) {
        System.out.println("[CONTROL] Reactivating profile: " + profileId);

        // Reactivate 不需要校验 User Admin，直接指挥 Entity 更新
        boolean success = SuspendUserProfile.updateStatusInDatabase(profileId, "active");

        if (success) {
            System.out.println("[CONTROL] Profile reactivated successfully");
        } else {
            System.err.println("[CONTROL] Failed to reactivate profile");
        }

        return success;
    }

    /**
     * 私有辅助方法：从数据库获取用户的角色名
     * @param profileId 用户 ID
     * @return 角色名，如果找不到返回 null
     */
    private String fetchRoleFromDatabase(String profileId) {
        String sql = "SELECT role FROM user_profile WHERE profile_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            System.err.println("[CONTROL] Error fetching role for profile: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}