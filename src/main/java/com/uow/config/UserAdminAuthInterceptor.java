package com.uow.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Restricts admin UI and admin APIs to logged-in users whose profile role is User Admin.
 */
@Component
public class UserAdminAuthInterceptor implements HandlerInterceptor {

    private static final String ROLE_SESSION = "role";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String role = readSessionRole(request);
        if (isUserAdmin(role)) {
            return true;
        }
        if (isApiRequest(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Access denied. User Admin role required.\"}");
            return false;
        }
        String msg = URLEncoder.encode("Access denied. Please sign in as User Admin.", StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/LoginPage.html?error=" + msg);
        return false;
    }

    private static String readSessionRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object r = session.getAttribute(ROLE_SESSION);
        return r instanceof String ? (String) r : null;
    }

    private static boolean isUserAdmin(String role) {
        return role != null && "User Admin".equalsIgnoreCase(role.trim());
    }

    private static boolean isApiRequest(HttpServletRequest request) {
        String path = requestPathWithoutContext(request);
        return path.startsWith("/api/");
    }

    private static String requestPathWithoutContext(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }
        if (uri.isEmpty()) {
            return "/";
        }
        return uri;
    }
}
