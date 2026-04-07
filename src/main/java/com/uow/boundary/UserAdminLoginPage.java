package com.uow.boundary;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uow.control.UserAdminLoginController;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserAdminLoginPage {

    @Autowired
    private UserAdminLoginController userAdminLoginController;

    public void displayPage() {
    }

    public void inputData() {
    }

    @PostMapping("/login")
    public String onLoginClick(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session) {

        String redirectPage = userAdminLoginController.processLogin(username, password);

        if (redirectPage != null) {
            session.setAttribute("user_id", userAdminLoginController.getUserAdminLoginAccount().getUser_id());
            session.setAttribute("username", userAdminLoginController.getUserAdminLoginAccount().getUsername());
            session.setAttribute("role", userAdminLoginController.getUserAdminLoginAccount().getRole());
            session.setAttribute("profile_id", userAdminLoginController.getUserAdminLoginAccount().getProfile_id());
            return "redirect:" + redirectPage;
        }

        String errorMessage = userAdminLoginController.getErrorMessage();
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        return "redirect:/LoginPage.html?error=" + encodedMessage;
    }

    public void showSuccessMessage() {
    }

    public void showErrorMessage(String msg) {
    }
}