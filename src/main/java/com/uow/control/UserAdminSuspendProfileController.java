package com.uow.control;

import com.uow.entity.SuspendUserProfile;
import com.uow.entity.ViewUserProfile;
import org.springframework.stereotype.Service;

/**
 * 挂起用户资料的 Control 层
 * 责任：协调 Entity 执行业务逻辑，处理状态转换规则
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

        ViewUserProfile view = new ViewUserProfile(profileId);
        if (view.getFromPFDatabase() == null) {
            System.err.println("[CONTROL] Failed to suspend profile: not found");
            return SuspendProfileOutcome.NOT_FOUND;
        }
        if (isUserAdminRole(view.getRoleName())) {
            System.err.println("[CONTROL] Cannot suspend User Admin profile");
            return SuspendProfileOutcome.USER_ADMIN_FORBIDDEN;
        }

        // 调用 Entity 层执行数据库更新
        SuspendUserProfile suspendEntity = new SuspendUserProfile(profileId, "suspended");
        boolean success = suspendEntity.updateStatusInDatabase();

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
