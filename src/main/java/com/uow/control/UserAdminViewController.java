package com.uow.control;

import org.springframework.stereotype.Service;

import com.uow.entity.ViewUserProfile;
import com.uow.util.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 负责用户资料查看用例的控制层。
 * 该服务现在是整个流程逻辑和数据访问（DAO功能）的唯一存放地，
 * 确保了无状态和非冗余的设计。所有对 Entity 对象的方法调用已被删除。
 */
@Service
public class UserAdminViewController {

    /**
     * 获取、格式化并返回指定用户资料 ID 的显示字符串。
     * 这个单一方法现在独自处理整个端到端的逻辑：从数据获取到显示格式化。
     * @param profileId 要检索的用户资料 ID。
     * @return 格式化后的显示字符串，如果未找到则返回“Profile not found with ID...”。
     */
    public String getProfile(String profileId) {
        System.out.println("[CONTROL] 控制层逻辑：检索 ID 为: " + profileId + " 的用户资料");

        // 直接从数据库获取数据。不再有状态式的 Entity 对象实例化和填充。
        ViewUserProfile fetchedProfile = fetchFromPFDatabase(profileId);

        // 格式化并返回用户资料数据用于显示
        if (fetchedProfile != null) {
            String result = "[DISPLAY] Profile ID: " + fetchedProfile.getProfileId()
                    + "\n[DISPLAY] Role Name: " + fetchedProfile.getRoleName()
                    + "\n[DISPLAY] Status: " + fetchedProfile.getStatus();
            return result;
        } else {
            return "[DISPLAY] 找不到 ID 为: " + profileId + " 的用户资料。";
        }
    }

    /**
     * 一个私有的、无状态的辅助方法，用于从数据库获取数据并创建一个新的、干净的 Entity 对象。
     * 这个方法封装了控制层内的数据访问 (DAO) 逻辑。
     * @param profileId 用于查询的用户资料 ID。
     * @return 如果找到则返回一个包含数据的 ViewUserProfile 对象，否则返回 null。
     */
    private ViewUserProfile fetchFromPFDatabase(String profileId) {
        String sql = "SELECT profile_id, role, p_status FROM user_profile WHERE profile_id = ?";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, profileId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 创建并返回一个新的、完整的 ViewUserProfile 对象。不再修改对象自身状态。
                    return new ViewUserProfile(
                        rs.getString("profile_id"),
                        rs.getString("role"),
                        rs.getString("p_status")
                    );
                } else {
                    System.err.println("[CONTROL] 找不到 ID 为: " + profileId + " 的用户资料。");
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("[CONTROL] 数据库数据获取失败！原因: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
