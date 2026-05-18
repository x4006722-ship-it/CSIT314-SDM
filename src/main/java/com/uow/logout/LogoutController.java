package com.uow.logout;

import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;

/**
 * Handles session invalidation during logout.
 * 
 * Responsibilities:
 * - Invalidate HttpSession objects
 * - Support multiple session data formats (direct HttpSession or wrapped in Map)
 * - Return success/failure status of logout operation
 * 
 * Usage: Called by LogoutPage to perform session cleanup when users log out.
 */
@Controller
public class LogoutController {

    /**
     * Invalidates a user's session to log them out.
     * 
     * @param sessionData Either an HttpSession directly or a Map containing session information
     * @return true if session was successfully invalidated, false otherwise
     */
    /**
     * Invalidates a user's session to log them out.
     * 
     * @param sessionData Either an HttpSession directly or a Map containing session information
     * @return true if session was successfully invalidated, false otherwise
     */
    public boolean logout(Object sessionData) {
        // Handle direct HttpSession object
        if (sessionData instanceof HttpSession session) {
            session.invalidate();
            return true;
        }
        // Handle HttpSession wrapped in a Map
        if (sessionData instanceof java.util.Map<?, ?> map) {
            Object rawSession = map.get("session");
            if (rawSession instanceof HttpSession session) {
                session.invalidate();
                return true;
            }
        }
        return false;
    }
}

