package com.uow.useraccount;

import org.springframework.stereotype.Controller;

/**
 * Retrieves user account information for display.
 * 
 * Responsibilities:
 * - Query account details by user ID
 * - Return complete account information including linked profile/role
 */
@Controller
public class ViewUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    /**
     * Retrieves a user account's complete information.
     * 
     * @param userId The user ID to retrieve
     * @return A Map with account details (user_id, username, email, etc.), or null if not found
     */
    public Object viewAccount(int userId) {
        return userAccount.getViewAccount(userId);
    }
}
