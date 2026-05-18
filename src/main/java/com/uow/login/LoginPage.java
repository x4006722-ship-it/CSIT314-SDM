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

@Controller
public class LoginPage {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    @Autowired
    private LoginController loginController;

    @GetMapping("/login")
    public String showLoginPage() {
        return "forward:/LoginPage.html";
    }

    public String showLoginErrorMessage(String message) {
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        return "redirect:/LoginPage.html?error=" + encoded;
    }

    @PostMapping("/login")
    public String userLogin(@RequestParam Map<String, String> loginMap, HttpSession session) {
        String username = loginMap.get("username") == null ? "" : loginMap.get("username").trim();
        String password = loginMap.get("password") == null ? "" : loginMap.get("password").trim();

        if (username.isBlank() || password.isBlank()) {
            return showLoginErrorMessage("Empty field detected.");
        }
        if (password.length() < 3) {
            return showLoginErrorMessage("Password must be at least 3 characters.");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return showLoginErrorMessage("Invalid username format.");
        }

        Object result = loginController.login(loginMap);
        if (!(result instanceof Map<?, ?> sessionMap) || sessionMap.get("error") != null) {
            String error = (result instanceof Map<?, ?> m && m.get("error") != null)
                    ? String.valueOf(m.get("error")) : "Login failed.";
            return showLoginErrorMessage(error);
        }

        String role = sessionMap.get("role") == null ? "" : String.valueOf(sessionMap.get("role")).trim();
        Object userIdRaw = sessionMap.get("userId");
        int userId = userIdRaw instanceof Number n ? n.intValue() : 0;

        session.setAttribute("username", username);
        session.setAttribute("role", role);
        session.setAttribute("userId", userId);

        return redirectPage(role);
    }

    private String redirectPage(String role) {
        if ("User Admin".equalsIgnoreCase(role))          return "redirect:/ManageProfile.html";
        if ("Fund Raiser".equalsIgnoreCase(role))         return "redirect:/FundRaiserPage.html";
        if ("Donee".equalsIgnoreCase(role))               return "redirect:/DoneePage.html";
        if ("Platform Management".equalsIgnoreCase(role)) return "redirect:/PlatformPage.html";
        return "redirect:/LoginPage.html";
    }
}
