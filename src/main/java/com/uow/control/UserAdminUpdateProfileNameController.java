package com.uow.control;

import com.uow.entity.CreateUserProfile;
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
     * @return null 成功；否则为错误信息（含 {@link CreateUserProfile#MSG_ROLE_ALREADY_EXISTS}）
     */
    public String updateRoleName(String profileId, String newRoleName) {
        System.out.println("[CONTROL] Updating profile " + profileId + " role name to: " + newRoleName);

        if (profileId == null || profileId.trim().isEmpty()) {
            System.err.println("[CONTROL] Invalid profileId");
            return "Invalid profile id.";
        }

        if (newRoleName == null || newRoleName.trim().isEmpty()) {
            System.err.println("[CONTROL] Invalid newRoleName");
            return "Role name is required.";
        }

        UpdateUserProfileName updateEntity = new UpdateUserProfileName(profileId, newRoleName);
        String err = updateEntity.updateRoleNameInDatabase();

        if (err == null) {
            System.out.println("[CONTROL] Role name updated successfully");
        } else {
            System.err.println("[CONTROL] Failed to update role name: " + err);
        }

        return err;
    }
}
