package com.uow.control;

import com.uow.entity.UserAccount;

public class LoginController {

    public String processLogin(String username, String password) {
        if (!UserAccount.verifyCredentials(username, password)) {
            return "Invalid username or password"; 
        }

        if (!UserAccount.checkAccountStatus(username)) {
            return "Account suspended";
        }

        return "Success"; 
    }

    public boolean validateSession() {
        return true;
    }
}