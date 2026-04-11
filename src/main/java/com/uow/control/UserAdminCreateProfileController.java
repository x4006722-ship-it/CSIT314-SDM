package com.uow.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.uow.entity.CreateUserProfile;
import com.uow.util.DBUtils;

@Service
public class UserAdminCreateProfileController {

    // ... (保留你现有的 getAllProfiles 方法) ...

    /**
     * 创建用户资料的核心业务逻辑。
     * @param roleName 角色名
     * @param status 状态
     * @return 错误信息字符串，如果成功则返回 null
     */
    public String createProfile(String roleName, String status) {
        System.out.println("[CONTROL] Processing create request for role: " + roleName);

        if (roleName == null || roleName.trim().isEmpty()) {
            return "Role name cannot be empty";
        }

        // 1. 业务校验：检查角色名是否已存在
        if (existsRoleNameIgnoreCase(roleName)) {
            System.err.println("[CONTROL] Role creation failed: duplicate role detected.");
            return CreateUserProfile.MSG_ROLE_ALREADY_EXISTS;
        }

        // 2. 校验通过，指挥 Entity 执行插入
        boolean success = CreateUserProfile.saveToPFDatabase(roleName, status);

        if (success) {
            System.out.println("[CONTROL] Profile created successfully.");
            return null; // Return null indicates success
        } else {
            return "Failed to save profile to database.";
        }
    }

    /**
     * 业务逻辑：检查是否存在重复的角色名（忽略大小写和空格）
     * （从 Entity 层转移至此，并进行了逻辑保留）
     */
    private boolean existsRoleNameIgnoreCase(String roleName) {
        String target = normalizeRoleKey(roleName);
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
            System.err.println("[CONTROL] Duplicate role check failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 辅助逻辑：规范化角色名称以进行比较
     * （从 Entity 层转移至此）
     */
    private String normalizeRoleKey(String roleName) {
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

    /**
     * 获取所有的用户资料列表 (收拢了原先写在 Entity 里的逻辑)
     */
    public java.util.List<com.uow.entity.ViewUserProfile> getAllProfiles() {
        java.util.List<com.uow.entity.ViewUserProfile> profiles = new java.util.ArrayList<>();
        
        // 查询数据库的 SQL 语句
        String sql = "SELECT profile_id, role, p_status FROM user_profile";
        
        try (java.sql.Connection conn = com.uow.util.DBUtils.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            
            // 遍历结果，将每一行数据打包成 ViewUserProfile 对象
            while (rs.next()) {
                profiles.add(new com.uow.entity.ViewUserProfile(
                    rs.getString("profile_id"),
                    rs.getString("role"),
                    rs.getString("p_status")
                ));
            }
            System.out.println("[CONTROL] 成功从数据库获取了 " + profiles.size() + " 个 profile");
            
        } catch (java.sql.SQLException e) {
            System.err.println("[CONTROL] 获取 Profile 列表失败! 原因: " + e.getMessage());
            e.printStackTrace();
        }
        
        return profiles;
    }
}
