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

        if (userAccount.getViewAccount(userId) == null) {
            return false;
        }

        String username = text(m.get("username"));
        String fullName = text(m.get("fullName"));
        String email = text(m.get("email"));
        String phoneNumber = text(m.get("phoneNumber"));
        String password = text(m.get("password"));
        String accountStatus = text(m.get("accountStatus"));
        String profileIdText = text(m.get("profileId"));

        // All fields required — blank means the user cleared a mandatory field
        if (username.isBlank() || fullName.isBlank() || email.isBlank() || phoneNumber.isBlank()
                || password.isBlank() || accountStatus.isBlank() || profileIdText.isBlank()) {
            return false;
        }

        // Same format/length rules as create
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }
        if (phoneNumber.length() < 8) {
            return false;
        }
        if (password.length() < 3) {
            return false;
        }

        int profileId = parseInt(profileIdText);
        if (profileId <= 0) {
            return false;
        }

        // Duplicate check on update — exclude the account currently being edited
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

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
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

}
