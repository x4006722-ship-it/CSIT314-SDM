package com.uow.control;

import org.springframework.stereotype.Controller;

import com.uow.entity.UserAdminLoginAccount;

@Controller
public class UserAdminLoginController {

    private final UserAdminLoginAccount userAdminLoginAccount = new UserAdminLoginAccount();
    private String errorMessage = "";

    public String processLogin(String username, String userPassword) {
        boolean credentialsValid = userAdminLoginAccount.verifyCredentials(username, userPassword);

        if (!credentialsValid) {
            errorMessage = "Invalid username or password.";
            return null;
        }

        if (!userAdminLoginAccount.checkAccountStatus()) {
            errorMessage = "Account suspended.";
            return null;
        }

        if (!userAdminLoginAccount.checkProfileStatus()) {
            errorMessage = "Role suspended.";
            return null;
        }

        String redirectPage = userAdminLoginAccount.getRedirectPageByRole();

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

    public UserAdminLoginAccount getUserAdminLoginAccount() {
        return userAdminLoginAccount;
    }
}