package com.uow.login;

import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    private final Login loginEntity = new Login();

    private String errorMessage = "";
    private String role = null;
    private int userId = 0;

    public String getErrorMessage() { return errorMessage; }
    public String getRole() { return role; }
    public int getUserId() { return userId; }

    public boolean login(String username, String password) {
        role = null;
        userId = 0;
        errorMessage = "";

        if (username == null || username.isBlank()) {
            errorMessage = "Username is required.";
            return false;
        }
        if (password == null || password.isBlank()) {
            errorMessage = "Password is required.";
            return false;
        }
        if (!loginEntity.verifyLogin(username, password)) {
            errorMessage = "Invalid credentials or account suspended.";
            return false;
        }

        role = loginEntity.getRole();
        userId = loginEntity.getUserId();
        return true;
    }

    public String getRedirectPage() {
        if (role == null) return "/LoginPage.html";
        if ("User Admin".equalsIgnoreCase(role)) return "/ManageProfile.html";
        if ("Fund Raiser".equalsIgnoreCase(role)) return "/FundRaiserPage.html";
        if ("Donee".equalsIgnoreCase(role)) return "/DoneePage.html";
        if ("Platform Management".equalsIgnoreCase(role)) return "/PlatformPage.html";
        return "/LoginPage.html";
    }
}

