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
    public void onLoginClick(
    @RequestParam("username") String username, 
    @RequestParam("password") String password, 
    HttpServletResponse response) throws IOException {
        
        String result = loginController.processLogin(username, password);

        if ("Success".equals(result)) {
            response.sendRedirect("/admin_dashboard.html");
        } else {
            response.getWriter().write("Login Failed: " + result);
        }
    }
}