package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.uow.util.DBUtils;

/**
 * 搜索用户资料的 Entity 类
 * 责任：直接执行数据库查询，返回 ProfileDTO 列表
 */
public class SearchUserProfile {

    /**
     * 根据关键词和状态搜索数据库中的用户资料
     * @param keyword 搜索关键词（角色名）
     * @param status 状态过滤（"active", "suspended", "all"）
     * @return ProfileDTO 列表
     */
    public static List<ViewUserProfile> searchDatabase(String keyword, String status) {
        List<ViewUserProfile> results = new ArrayList<>();
        
        // 动态拼装 SQL：1=1 是为了方便后面拼装 AND 条件
        StringBuilder sql = new StringBuilder("SELECT profile_id, role, p_status FROM user_profile WHERE 1=1 ");
        
        // 如果有关键词，添加 LIKE 条件
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND role LIKE ? ");
        }
        
        // 如果选择了特定状态（不是 "all"），添加状态过滤条件
        if (status != null && !status.equalsIgnoreCase("all")) {
            sql.append("AND p_status = ? ");
        }

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            // 绑定参数
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + keyword + "%");
            }
            if (status != null && !status.equalsIgnoreCase("all")) {
                pstmt.setString(paramIndex++, status);
            }

            // 执行查询并收集结果
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new ViewUserProfile(
                        rs.getString("profile_id"),
                        rs.getString("role"),
                        rs.getString("p_status")
                    ));
                }
            }
            System.out.println("Search completed. Found " + results.size() + " profiles.");
        } catch (SQLException e) {
            System.err.println("Database search failed: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
}