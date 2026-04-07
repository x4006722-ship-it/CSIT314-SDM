package com.uow.control;

import org.springframework.stereotype.Controller;

import com.uow.entity.UserAdminLoginAccount;

@Controller
public class UserAdminLoginController {

    private final UserAdminLoginAccount userAdminLoginAccount = new UserAdminLoginAccount();
    private String errorMessage = "";

    public String processLogin(String username, String password) {
        boolean credentialsValid = userAdminLoginAccount.verifyCredentials(username, password);

        if (!credentialsValid) {
            errorMessage = "Invalid username or password.";
            return null;
        }

        if (!userAdminLoginAccount.checkAccountStatus()) {
            errorMessage = "Your account has been suspended. Please contact the platform management.";
            return null;
        }

        String redirectPage = userAdminLoginAccount.getRedirectPageByRole();

        if (redirectPage == null) {
            errorMessage = "Unauthorized role.";
            return null;
        }

        return redirectPage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public UserAdminLoginAccount getUserAdminLoginAccount() {
        return userAdminLoginAccount;
    }
}