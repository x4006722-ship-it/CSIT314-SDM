package com.uow.useraccount;

import org.springframework.stereotype.Controller;

/**
 * Handles suspension (activation toggle) of user accounts.
 * 
 * Responsibilities:
 * - Prevent users from suspending themselves
 * - Validate both current and target user accounts
 * - Prevent suspension of User Admin role accounts
 * - Toggle account status between Active and Suspended
 */
@Controller
public class SuspendUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    /**
     * Toggles the suspension status of a user account.
     * 
     * Business Rules:
     * - A user cannot suspend themselves
     * - Cannot suspend User Admin role accounts
     * - Both accounts must exist and be valid
     * 
     * @param targetUserId The user ID to suspend/activate
     * @param currentUserId The user ID performing the action
     * @return true if suspension toggle was successful, false if validation fails
     */
    public boolean suspendAccount(int targetUserId, int currentUserId) {
        if (targetUserId == currentUserId) {
            return false;
        }

        Object currentAccount = userAccount.getViewAccount(currentUserId);
        if (!(currentAccount instanceof java.util.Map<?, ?>)) {
            return false;
        }

        Object targetAccount = userAccount.getViewAccount(targetUserId);
        if (!(targetAccount instanceof java.util.Map<?, ?> targetMap)) {
            return false;
        }

        Object roleName = targetMap.get("roleName");
        if (roleName != null && "User Admin".equalsIgnoreCase(String.valueOf(roleName).trim())) {
            return false;
        }

        return userAccount.saveSuspendAccount(targetUserId, currentUserId);
    }
}
