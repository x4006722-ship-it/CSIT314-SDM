package com.uow.boundary;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uow.control.LoginController;

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
        if (!loginController.validateInput(username, password)) {
            return showLoginErrorMessage();
        }

        loginController.login(username, password);
        if (loginController.errorMessage != null && !loginController.errorMessage.isBlank()) {
            return showLoginErrorMessage();
        }

        session.setAttribute("username", username);
        session.setAttribute("role", loginController.role);
        return redirectPage();
    }

    public String redirectPage() {
        String role = loginController.role;
        if (role == null) {
            return "redirect:/LoginPage.html";
        }
        if ("User Admin".equalsIgnoreCase(role)) {
            return "redirect:/ManageProfile.html";
        }
        if ("Fund Raiser".equalsIgnoreCase(role)) {
            return "redirect:/FundRaiserPage.html";
        }
        if ("Donee".equalsIgnoreCase(role)) {
            return "redirect:/DoneePage.html";
        }
        if ("Platform Management".equalsIgnoreCase(role)) {
            return "redirect:/PlatformPage.html";
        }
        String msg = URLEncoder.encode("Invalid role.", StandardCharsets.UTF_8);
        return "redirect:/LoginPage.html?error=" + msg;
    }

    public String showLoginErrorMessage() {
        String msg = loginController.errorMessage == null ? "Login failed." : loginController.errorMessage;
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        return "redirect:/LoginPage.html?error=" + encoded;
    }
}

