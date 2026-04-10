package com.uow.control;

import com.uow.entity.SuspendUserProfile;
import org.springframework.stereotype.Service;

/**
 * 挂起用户资料的 Control 层
 * 责任：协调 Entity 执行业务逻辑，处理状态转换规则
 */
@Service
public class UserAdminSuspendProfileController {

    /**
     * 挂起（暂停）一个 profile
     * @param profileId 要挂起的 profile ID
     * @return 操作是否成功
     */
    public boolean suspendProfile(String profileId) {
        System.out.println("[CONTROL] Suspending profile: " + profileId);

        // 调用 Entity 层执行数据库更新
        SuspendUserProfile suspendEntity = new SuspendUserProfile(profileId, "suspended");
        boolean success = suspendEntity.updateStatusInDatabase();

        if (success) {
            System.out.println("[CONTROL] Profile suspended successfully");
        } else {
            System.err.println("[CONTROL] Failed to suspend profile");
        }

        return success;
    }

    /**
     * 重新激活一个 profile
     * @param profileId 要激活的 profile ID
     * @return 操作是否成功
     */
    public boolean reactivateProfile(String profileId) {
        System.out.println("[CONTROL] Reactivating profile: " + profileId);

        // 调用 Entity 层执行数据库更新
        SuspendUserProfile activateEntity = new SuspendUserProfile(profileId, "active");
        boolean success = activateEntity.updateStatusInDatabase();

        if (success) {
            System.out.println("[CONTROL] Profile reactivated successfully");
        } else {
            System.err.println("[CONTROL] Failed to reactivate profile");
        }

        return success;
    }
}
