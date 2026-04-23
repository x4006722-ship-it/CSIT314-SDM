package com.uow.logout;

import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}

