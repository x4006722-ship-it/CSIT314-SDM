package com.uow.useraccount;

import org.springframework.stereotype.Controller;

@Controller
public class UpdateUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    public boolean updateAccount(Object updatedAccountData) {
        if (!(updatedAccountData instanceof java.util.Map<?, ?> m)) {
            return false;
        }

        int userId = parseInt(m.get("userId"));
        if (userId <= 0) {
            return false;
        }

        Object current = userAccount.getViewAccount(userId);
        if (!(current instanceof java.util.Map<?, ?> currentMap)) {
            return false;
        }

        String username = nonBlankOrDefault(m.get("username"), currentMap.get("username"));
        String fullName = nonBlankOrDefault(m.get("fullName"), currentMap.get("full_name"));
        String email = nonBlankOrDefault(m.get("email"), currentMap.get("email"));
        String phoneNumber = nonBlankOrDefault(m.get("phoneNumber"), currentMap.get("phone_number"));
        String password = nonBlankOrDefault(m.get("password"), currentMap.get("password"));
        String accountStatus = nonBlankOrDefault(m.get("accountStatus"), currentMap.get("a_status"));
        int profileId = parseInt(nonBlankOrDefault(m.get("profileId"), currentMap.get("profile_id")));

        // 【核心修复】：更新时查重，必须排除当前正在编辑的账号ID
        if (userAccount.isDuplicateAccount(username, email, phoneNumber, userId)) {
            return false;
        }

        java.util.Map<String, Object> updateData = new java.util.HashMap<>();
        updateData.put("userId", userId);
        updateData.put("username", username);
        updateData.put("fullName", fullName);
        updateData.put("email", email);
        updateData.put("phoneNumber", phoneNumber);
        updateData.put("password", password);
        updateData.put("accountStatus", accountStatus);
        updateData.put("profileId", profileId);
        return userAccount.saveUpdateAccount(updateData);
    }

    private int parseInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String nonBlankOrDefault(Object incoming, Object fallback) {
        String in = incoming == null ? "" : String.valueOf(incoming).trim();
        if (!in.isBlank()) {
            return in;
        }
        return fallback == null ? "" : String.valueOf(fallback).trim();
    }
}
