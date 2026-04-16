package com.uow.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.uow.control.LogoutController;

@Controller
public class LogoutPage {

    @Autowired
    private LogoutController logoutController;

    @GetMapping("/logout")
    public String userLogout() {
        logoutController.logout();
        return showLoginPage();
    }

    public String showLoginPage() {
        return "redirect:/LoginPage.html";
    }
}

