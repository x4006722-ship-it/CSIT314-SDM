package com.uow.control;

import org.springframework.stereotype.Controller;

import com.uow.entity.UserSession;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserAdminLogoutController {

    private final UserSession userSession = new UserSession();

    public void processLogout(HttpSession session) {
        userSession.invalidateSession(session);
    }

    public String redirectToLogoutPage() {
        return "/LogoutPage.html";
    }
}