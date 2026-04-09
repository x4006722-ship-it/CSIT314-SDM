package com.uow.control;

import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {

    public void logout(HttpSession session) {
        session.invalidate();
    }
}