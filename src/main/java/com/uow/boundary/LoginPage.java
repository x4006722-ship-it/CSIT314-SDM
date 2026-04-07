package com.uow.boundary;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uow.control.LoginController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class LoginPage {
    
    private LoginController loginController = new LoginController();

    @PostMapping("/login")
    public void onLoginClick(@RequestParam("username") String username, 
                            @RequestParam("password") String password, 
                            HttpServletResponse response) throws IOException {
        
        String role = loginController.processLogin(username, password);

        response.setContentType("text/html;charset=UTF-8");

        if ("User Admin".equalsIgnoreCase(role)) {
            response.sendRedirect("/admin_dashboard.html");
        } 
        else if ("Fund Raiser".equalsIgnoreCase(role)) {
            response.sendRedirect("/fundraiser_dashboard.html");
        } 
        else if ("Donee".equalsIgnoreCase(role)) {
            response.sendRedirect("/donee_dashboard.html");
        } 
        else if ("Platform Management".equalsIgnoreCase(role)) {
            response.sendRedirect("/platform_dashboard.html");
        } 
        else {
            String alertScript = "<script>" +
                                "alert('Login Failed: Invalid credentials or account suspended.');" +
                                "window.location.href='/login.html';" +
                                "</script>";
            response.getWriter().write(alertScript);
        }
    }
}