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

        Object roleName = targetMap.get("roleName");
        if (roleName != null && "User Admin".equalsIgnoreCase(String.valueOf(roleName).trim())) {
            return false;
        }

        return userAccount.saveSuspendAccount(targetUserId, currentUserId);
    }
}
