package com.uow.control;

import com.uow.entity.UpdateUserProfileName;
import org.springframework.stereotype.Service;

/**
 * 更新用户资料名称的 Control 层
 * 责任：协调 Entity 执行业务逻辑
 */
@Service
public class UserAdminUpdateProfileNameController {

    /**
     * 更新 profile 的 role 名称
     * @param profileId 要更新的 profile ID
     * @param newRoleName 新的 role name
     * @return 操作是否成功
     */
    public boolean updateRoleName(String profileId, String newRoleName) {
        System.out.println("[CONTROL] Updating profile " + profileId + " role name to: " + newRoleName);

        // 验证输入
        if (profileId == null || profileId.trim().isEmpty()) {
            System.err.println("[CONTROL] Invalid profileId");
            return false;
        }

        if (newRoleName == null || newRoleName.trim().isEmpty()) {
            System.err.println("[CONTROL] Invalid newRoleName");
            return false;
        }

        // 调用 Entity 层执行数据库更新
        UpdateUserProfileName updateEntity = new UpdateUserProfileName(profileId, newRoleName);
        boolean success = updateEntity.updateRoleNameInDatabase();

        if (success) {
            System.out.println("[CONTROL] Role name updated successfully");
        } else {
            System.err.println("[CONTROL] Failed to update role name");
        }

        return success;
    }
}
