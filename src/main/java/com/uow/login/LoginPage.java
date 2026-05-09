package com.uow.login;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class LoginPage {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    @Autowired
    private LoginController loginController;

    private String loginErrorMessage = "Login failed.";

    @GetMapping("/login")
    public String showLoginPage() {
        return "forward:/LoginPage.html";
    }

    @PostMapping("/login")
    public Object userLogin(@RequestParam Map<String, String> loginMap) {
        Object loginData = loginMap;

        String username = readText(loginMap.get("username"));
        String password = readText(loginMap.get("password"));

        if (username.isBlank() || password.isBlank()) {
            loginErrorMessage = "Empty field detected.";
            return showLoginErrorMessage();
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            loginErrorMessage = "Invalid username format.";
            return showLoginErrorMessage();
        }

        Object sessionObject = loginController.login(loginData);
        if (!(sessionObject instanceof Map<?, ?>)) {
            loginErrorMessage = "Login failed.";
            return showLoginErrorMessage();
        }

        Map<?, ?> sessionMap = (Map<?, ?>) sessionObject;
        if (sessionMap.get("error") != null) {
            loginErrorMessage = String.valueOf(sessionMap.get("error"));
            return showLoginErrorMessage();
        }

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpSession session = attributes.getRequest().getSession();
            session.setAttribute("username", username);
            session.setAttribute("role", readText(sessionMap.get("role")));
            session.setAttribute("userId", parseInt(sessionMap.get("userId")));
        }
        return redirectPage(readText(sessionMap.get("role")));
    }

    public String redirectPage(String role) {
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
        return "redirect:/LoginPage.html";
    }

    public String showLoginErrorMessage() {
        String encoded = URLEncoder.encode(loginErrorMessage, StandardCharsets.UTF_8);
        return "redirect:/LoginPage.html?error=" + encoded;
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int parseInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(readText(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

