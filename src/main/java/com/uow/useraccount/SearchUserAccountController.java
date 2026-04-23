package com.uow.useraccount;

import org.springframework.stereotype.Controller;

@Controller
public class SearchUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    public Object searchAccount(String username, String fullName, String email, int phoneNumber, String status, int profileID) {
        return userAccount.getSearchAccount(username, fullName, email, phoneNumber, status, profileID);
    }
}
