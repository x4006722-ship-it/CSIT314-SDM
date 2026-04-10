package com.uow.boundary;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uow.control.LoginController;

import jakarta.servlet.http.HttpSession;

@Controller
/**
 * Boundary for login form submission.
 * On success, stores basic user session attributes and redirects to the role page.
 */
public class LoginPage {

    @Autowired
    private LoginController LoginController;

    @PostMapping("/login")
    public String onLoginClick(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session) {

        String redirectPage = LoginController.processLogin(username, password);

        if (redirectPage != null) {
            session.setAttribute("user_id", LoginController.getLogin().getUser_id());
            session.setAttribute("username", LoginController.getLogin().getUsername());
            session.setAttribute("role", LoginController.getLogin().getRole());
            session.setAttribute("profile_id", LoginController.getLogin().getProfile_id());
            return "redirect:" + redirectPage;
        }

        String errorMessage = LoginController.getErrorMessage();
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        return "redirect:/LoginPage.html?error=" + encodedMessage;
    }
}