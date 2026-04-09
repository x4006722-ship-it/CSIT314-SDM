package com.uow.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.uow.control.LogoutController;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutPage {

    @Autowired
    private LogoutController logoutController;

    @GetMapping("/logout")
    public String onLogoutClick(HttpSession session) {
        logoutController.logout(session);
        return "redirect:/LoginPage.html";
    }

    public void clickLogout() {
    }

    public void redirectToLoginPage() {
    }
}