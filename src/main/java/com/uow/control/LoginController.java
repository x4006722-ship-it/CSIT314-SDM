package com.uow.control;

import com.uow.entity.UserAccount;

public class LoginController {

    public String processLogin(String username, String password) {
        String role = UserAccount.getRoleIfValid(username, password);

        if (role != null) {
            return role; 
        } else {
            return "Invalid";
        }
    }
}