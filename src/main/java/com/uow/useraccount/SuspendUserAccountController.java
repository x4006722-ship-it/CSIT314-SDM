package com.uow.useraccount;

import org.springframework.stereotype.Controller;

@Controller
public class SuspendUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    private String errorMessage = "";

    public String getErrorMessage() { return errorMessage; }

    public boolean suspendAccount(int targetUserId, int currentUserId) {
        errorMessage = "";

        if (targetUserId <= 0) { errorMessage = "Invalid account."; return false; }
        if (currentUserId <= 0) { errorMessage = "Session invalid."; return false; }
        if (targetUserId == currentUserId) { errorMessage = "Cannot suspend your own account."; return false; }

        if (!userAccount.saveSuspendAccount(targetUserId, currentUserId)) {
            errorMessage = userAccount.lastErrorMessage;
            return false;
        }
        return true;
    }
}
