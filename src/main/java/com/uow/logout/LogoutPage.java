package com.uow.logout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutPage {

    @Autowired
    private LogoutController logoutController;

    @GetMapping("/logout")
    public String userLogout(HttpSession session) {
        logoutController.logout(session);
        return showLoginPage();
    }

    public String showLoginPage() {
        return "redirect:/LoginPage.html";
    }
}

