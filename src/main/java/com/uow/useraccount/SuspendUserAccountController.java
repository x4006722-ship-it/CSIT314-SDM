package com.uow.useraccount;

import org.springframework.stereotype.Controller;

@Controller
public class SuspendUserAccountController {

    private final UserAccount userAccount = new UserAccount();

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

        // User Admin accounts must not be suspended (active → suspended). Re-activate is still allowed.
        if (isUserAdminRole(targetMap) && isActiveAccount(targetMap)) {
            return false;
        }

        return userAccount.saveSuspendAccount(targetUserId, currentUserId);
    }

    private static boolean isUserAdminRole(java.util.Map<?, ?> account) {
        Object r = account.get("roleName");
        if (r == null) {
            r = account.get("role");
        }
        String role = r == null ? "" : String.valueOf(r).trim().replaceAll("\\s+", " ");
        return "User Admin".equalsIgnoreCase(role);
    }

    private static boolean isActiveAccount(java.util.Map<?, ?> account) {
        Object st = account.get("a_status");
        if (st == null) {
            return true;
        }
        return !"suspended".equalsIgnoreCase(String.valueOf(st).trim());
    }
}
