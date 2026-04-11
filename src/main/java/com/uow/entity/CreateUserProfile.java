package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.uow.util.DBUtils;

/**
 * 负责执行创建用户资料数据库插入操作的 Entity 层。
 * 已剥离状态和业务校验逻辑，变为纯粹的 DAO 方法。
 */
public class CreateUserProfile {

    public static final String MSG_ROLE_ALREADY_EXISTS = "Role already exists";

    /**
     * 将新的角色资料插入数据库。
     * 此方法不包含任何业务校验，直接执行。校验应在 Control 层完成。
     *
     * @param roleName 角色名称
     * @param status 初始状态
     * @return 插入是否成功
     */
    public static boolean saveToPFDatabase(String roleName, String status) {
        String sql = "INSERT INTO user_profile (role, p_status) VALUES (?, ?)";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 存入数据库时去除前后空格
            pstmt.setString(1, roleName != null ? roleName.trim() : null);
            pstmt.setString(2, status);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("[ENTITY] Database insert failed! Reason: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
