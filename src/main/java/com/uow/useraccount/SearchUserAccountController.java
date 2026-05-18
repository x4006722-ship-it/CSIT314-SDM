package com.uow.useraccount;

import org.springframework.stereotype.Controller;

/**
 * Handles searching for user accounts with multiple filter criteria.
 * 
 * Responsibilities:
 * - Accept search/filter parameters
 * - Delegate to UserAccount entity for database query
 * - Return list of matching accounts
 */
@Controller
public class SearchUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    /**
     * Searches for user accounts with optional filter criteria.
     * 
     * @param searchData A Map with optional filters: username, fullName, email, phoneNumber, status, profileID
     * @return List of Maps representing matching accounts
     */
    public Object searchAccount(Object searchData) {
        return userAccount.getSearchAccount(searchData);
    }
}
