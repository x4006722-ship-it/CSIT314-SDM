package com.uow.logout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

/**
 * Handles user logout requests and session termination.
 * 
 * Responsibilities:
 * - Process logout requests
 * - Invalidate user sessions
 * - Redirect users back to the login page
 * 
 * Usage: HTTP GET /logout processes the logout and clears the user's session.
 */
@Controller
public class LogoutPage {

    @Autowired
    private LogoutController logoutController;

    /**
     * Processes user logout request and invalidates their session.
     * 
     * @param sessionData The session object to be invalidated
     * @return Redirect to the login page
     */
    @GetMapping("/logout")
    public String userLogout(Object sessionData) {
        HttpSession session = null;
        // Extract the HttpSession object from the provided sessionData
        if (sessionData instanceof HttpSession httpSession) {
            session = httpSession;
        } else {
            // If sessionData is not a direct HttpSession, retrieve it from the request context
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

    /**
     * Redirects to the login page.
     * 
     * @return Redirect to LoginPage.html
     */
    public String showLoginPage() {
        return "redirect:/LoginPage.html";
    }
}

