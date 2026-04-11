package com.uow.control;

import com.uow.entity.UpdateUserProfileName;
import com.uow.util.DBUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * 更新用户资料名称的 Control 层
 * 责任：协调 Entity 执行业务逻辑，负责名称重复的业务校验
 */
@Service
public class UserAdminUpdateProfileNameController {

    public static final String MSG_ROLE_ALREADY_EXISTS = "Role already exists";

    /**
     * 更新 profile 的 role 名称
     * @param profileId 要更新的 profile ID
     * @param newRoleName 新的 role name
     * @return null 成功；否则为错误信息
     */
    public String updateRoleName(String profileId, String newRoleName) {
        System.out.println("[CONTROL] 准备更新 profile " + profileId + " 角色名为: " + newRoleName);

        if (profileId == null || profileId.trim().isEmpty()) {
            return "Invalid profile id.";
        }
        if (newRoleName == null || newRoleName.trim().isEmpty()) {
            return "Role name is required.";
        }

        String trimmedPid = profileId.trim();
        String trimmedNew = newRoleName.trim();

        // 1. 业务逻辑校验：检查名称是否与其他 profile 冲突
        if (isDuplicateRoleName(trimmedPid, trimmedNew)) {
            System.err.println("[CONTROL] 角色名更新失败：名称已存在冲突");
            return MSG_ROLE_ALREADY_EXISTS;
        }

        // 2. 校验通过，指挥 Entity 进行无条件的更新
        boolean success = UpdateUserProfileName.updateRoleNameInDatabase(trimmedPid, trimmedNew);

        if (success) {
            System.out.println("[CONTROL] 角色名称更新成功");
            return null;
        } else {
            return "Profile not found or database error.";
        }
    }

    /**
     * 私有辅助方法：检查新名称是否与【除了自己以外】的现有角色名称重复
     */
    private boolean isDuplicateRoleName(String profileId, String newRoleName) {
        String newKey = normalizeRoleKey(newRoleName);
        if (newKey.isEmpty()) return false;

        String sql = "SELECT profile_id, role FROM user_profile";
        
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                String existingId = String.valueOf(rs.getObject("profile_id"));
                // 排除自己，允许名称不改变的保存
                if (existingId.equals(profileId)) {
                    continue;
                }
                
                String existingRoleKey = normalizeRoleKey(rs.getString("role"));
                if (newKey.equals(existingRoleKey)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CONTROL] 查重失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 辅助逻辑：规范化角色名称以进行比较 (去除空格转小写)
     */
    private String normalizeRoleKey(String roleName) {
        if (roleName == null) return "";
        StringBuilder sb = new StringBuilder(roleName.length());
        for (int i = 0; i < roleName.length(); i++) {
            char c = roleName.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString().toLowerCase(Locale.ROOT);
    }
}
