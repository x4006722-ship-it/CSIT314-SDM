package com.uow.control;

import org.springframework.stereotype.Controller;

import com.uow.entity.Login;

@Controller
public class LoginController {

    private final Login login = new Login();
    private String errorMessage = "";

    public String processLogin(String username, String userPassword) {
        boolean credentialsValid = login.verifyCredentials(username, userPassword);

        if (!credentialsValid) {
            errorMessage = "Invalid username or password.";
            return null;
        }

        if (!login.checkAccountStatus()) {
            errorMessage = "Account suspended.";
            return null;
        }

        if (!login.checkProfileStatus()) {
            errorMessage = "Role suspended.";
            return null;
        }

        String redirectPage = login.getRedirectPageByRole();

        if (redirectPage == null) {
            errorMessage = "Invalid role.";
            return null;
        }

        errorMessage = "";
        return redirectPage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Login getLogin() {
        return login;
    }
}