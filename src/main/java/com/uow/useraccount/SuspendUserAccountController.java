package com.uow.useraccount;

import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class SuspendUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    public boolean suspendAccount(int targetUserId, int currentUserId) {
        // Business Domain Rule 1: Self-suspension is strictly forbidden
        if (targetUserId == currentUserId) {
            throw new IllegalArgumentException("You cannot suspend your own account.");
        }

        // Business Domain Rule 2: Verify account existence criteria inside the logic flow
        Object currentAccount = userAccount.getViewAccount(currentUserId);
        if (!(currentAccount instanceof Map<?, ?>)) {
            throw new IllegalArgumentException("Current user account not found.");
        }

        Object targetAccount = userAccount.getViewAccount(targetUserId);
        if (!(targetAccount instanceof Map<?, ?> targetMap)) {
            throw new IllegalArgumentException("Target account not found.");
        }

        // Business Domain Rule 3: Protected Role Exclusivity Check (User Admins cannot be locked out)
        Object roleName = targetMap.get("roleName");
        if (roleName != null && "User Admin".equalsIgnoreCase(String.valueOf(roleName).trim())) {
            throw new IllegalArgumentException("Accounts with the User Admin role cannot be suspended.");
        }

        // Logic validated, send instructions down to Entity update mechanisms
        return userAccount.saveSuspendAccount(targetUserId, currentUserId);
    }
}