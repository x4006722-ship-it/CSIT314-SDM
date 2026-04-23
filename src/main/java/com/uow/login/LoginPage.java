package com.uow.login;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginPage {

    @Autowired
    private LoginController loginController;

    @GetMapping("/login")
    public String showLoginPage() {
        return "forward:/LoginPage.html";
    }

    @PostMapping("/login")
    public String userLogin(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            HttpSession session) {
        if (!loginController.login(username, password)) {
            return showLoginErrorMessage();
        }

        session.setAttribute("username", username);
        session.setAttribute("role", loginController.getRole());
        session.setAttribute("userId", loginController.getUserId());
        return redirectPage();
    }

    public String redirectPage() {
        return "redirect:" + loginController.getRedirectPage();
    }

    public String showLoginErrorMessage() {
        String msg = loginController.getErrorMessage() == null ? "Login failed." : loginController.getErrorMessage();
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        return "redirect:/LoginPage.html?error=" + encoded;
    }
}

