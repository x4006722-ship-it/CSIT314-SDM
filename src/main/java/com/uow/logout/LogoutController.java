package com.uow.logout;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {

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

