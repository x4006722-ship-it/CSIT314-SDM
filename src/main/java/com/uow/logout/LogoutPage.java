package com.uow.logout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutPage {

    @Autowired
    private LogoutController logoutController;

    @GetMapping("/logout")
    public String userLogout(Object sessionData) {
        HttpSession session = null;
        if (sessionData instanceof HttpSession httpSession) {
            session = httpSession;
        } else {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                session = attributes.getRequest().getSession(false);
            }
        }

        if (session == null) {
            return showLoginPage();
        }
        logoutController.logout(session);
        return showLoginPage();
    }

    public String showLoginPage() {
        return "redirect:/LoginPage.html";
    }
}

