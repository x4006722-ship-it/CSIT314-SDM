package com.uow.login;

import java.util.Map;

import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    private final Login loginEntity = new Login();

    public Object login(Object loginData) {
        Object raw = loginEntity.verifyLogin(loginData);
        if (!(raw instanceof Map<?, ?> row)) {
            return Map.of("error", "Invalid credentials.");
        }

        String accountStatus = row.get("a_status") == null ? "" : String.valueOf(row.get("a_status")).trim();
        String profileStatus = row.get("p_status") == null ? "" : String.valueOf(row.get("p_status")).trim();
        if (!"Active".equalsIgnoreCase(accountStatus) || !"Active".equalsIgnoreCase(profileStatus)) {
            return Map.of("error", "Account or profile is not active.");
        }

        Object userIdRaw = row.get("user_id");
        int userId = userIdRaw instanceof Number n ? n.intValue() : 0;
        String role = row.get("role") == null ? "" : String.valueOf(row.get("role")).trim();

        return Map.of("userId", userId, "role", role);
    }
}
