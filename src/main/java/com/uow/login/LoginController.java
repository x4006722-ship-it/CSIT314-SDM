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

        String accountStatus = readText(row.get("a_status"));
        String profileStatus = readText(row.get("p_status"));
        if (!"Active".equalsIgnoreCase(accountStatus) || !"Active".equalsIgnoreCase(profileStatus)) {
            return Map.of("error", "Account or profile is not active.");
        }

        return Map.of(
                "userId", parseInt(row.get("user_id")),
                "role", readText(row.get("role"))
        );
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int parseInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(readText(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

