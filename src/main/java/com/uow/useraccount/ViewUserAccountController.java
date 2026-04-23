package com.uow.useraccount;

import org.springframework.stereotype.Controller;

@Controller
public class ViewUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    public Object viewAccount(int userID) {
        return userAccount.getViewAccount(userID);
    }
}
