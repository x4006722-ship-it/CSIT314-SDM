package com.uow.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.uow.control.UserAdminLogoutController;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserAdminLogoutPage {

    @Autowired
    private UserAdminLogoutController userAdminLogoutController;

    @GetMapping("/logout")
    public String clickLogoutButton(HttpSession session) {
        userAdminLogoutController.processLogout(session);
        return "redirect:" + userAdminLogoutController.redirectToLogoutPage();
    }

    public String displayLogoutPage() {
        return "redirect:/LogoutPage.html";
    }
}