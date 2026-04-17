package com.uow.control;

import org.springframework.stereotype.Controller;

import com.uow.entity.Login;

@Controller
public class LoginController {

    private final Login login = new Login();

    public String errorMessage = "";
    public String role = null;
    public int userId = 0;

    public void login(String username, String password) {
        userId = 0;
        boolean ok = login.verifyCredentials(username, password);
        if (!ok) {
            errorMessage = "Invalid username or password.";
            role = null;
            return;
        }

        if (!login.checkAccountStatus()) {
            errorMessage = "Account suspended.";
            role = null;
            return;
        }

        if (!login.checkProfileStatus()) {
            errorMessage = "Role suspended.";
            role = null;
            return;
        }

        role = login.role;
        userId = login.user_id;
        errorMessage = "";
    }

    public boolean validateInput(String username, String password) {
        if (username == null || username.isBlank()) {
            errorMessage = "Username is required.";
            return false;
        }
        if (password == null || password.isBlank()) {
            errorMessage = "Password is required.";
            return false;
        }
        return true;
    }
}

