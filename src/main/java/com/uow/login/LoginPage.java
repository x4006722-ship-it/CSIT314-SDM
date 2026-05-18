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

/**
 * Displays the login page and handles user login requests.
 * 
 * Responsibilities:
 * - Render the login HTML page
 * - Validate login form inputs (username, password format)
 * - Store session attributes upon successful authentication
 * - Route authenticated users to appropriate pages based on their role
 * - Display error messages for failed login attempts
 * 
 * Usage: HTTP GET /login displays the login page; POST /login processes login requests.
 */
@Controller
public class LoginPage {

    // Pattern for validating username: only alphanumeric and underscore characters allowed
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    @Autowired
    private LoginController loginController;

    // Stores the most recent login error message to display to the user
    private String loginErrorMessage = "Login failed.";

    /**
     * Displays the login page.
     * 
     * @return Forwards to LoginPage.html
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "forward:/LoginPage.html";
    }

    /**
     * Processes user login request.
     * 
     * @param loginMap Form data containing username and password
     * @return Redirect to user's dashboard if login succeeds, or redirect to login page with error if it fails
     */
    @PostMapping("/login")
    public Object userLogin(@RequestParam Map<String, String> loginMap) {
        Object loginData = loginMap;

        String username = readText(loginMap.get("username"));
        String password = readText(loginMap.get("password"));

        // Validate that username and password fields are not empty
        if (username.isBlank() || password.isBlank()) {
            loginErrorMessage = "Empty field detected.";
            return showLoginErrorMessage();
        }
        // Validate password meets minimum length requirement (at least 3 characters)
        if (password.length() < 3) {
            loginErrorMessage = "Password must be at least 3 characters.";
            return showLoginErrorMessage();
        }
        // Validate username format: only alphanumeric characters and underscores allowed
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

    /**
     * Routes users to their appropriate dashboard based on their role.
     * 
     * @param role The user's role/permission level
     * @return Redirect URL for the appropriate page based on role
     */
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

    /**
     * Redirects to login page with error message in query parameter.
     * 
     * @return Redirect to LoginPage.html with encoded error message
     */
    public String showLoginErrorMessage() {
        String encoded = URLEncoder.encode(loginErrorMessage, StandardCharsets.UTF_8);
        return "redirect:/LoginPage.html?error=" + encoded;
    }

    /**
     * Safely converts an Object to a trimmed String.
     * 
     * @param value The object to convert (can be null)
     * @return The trimmed string value, or empty string if value is null
     */
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

