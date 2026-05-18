package com.uow.logout;

import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {

    public boolean logout(HttpSession session) {
        if (session == null) {
            return false;
        }
        session.invalidate();
        return true;
    }
}
