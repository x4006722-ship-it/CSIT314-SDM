package com.uow.boundary;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uow.control.LoginController;

@RestController
public class LoginPage {
    
    private LoginController loginController = new LoginController();

    @PostMapping("/login")
    public String onLoginClick(@RequestParam String username, @RequestParam String password) {
        
        String result = loginController.processLogin(username, password);

        if ("Success".equals(result)) {
            return "showSuccessMessage() -> Login successful!";
        } else {
            return "showErrorMessage() -> " + result;
        }
    }
}