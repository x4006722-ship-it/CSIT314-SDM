package com.uow.useraccount;

import org.springframework.stereotype.Controller;

@Controller
public class UpdateUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    public boolean updateAccount(Object updatedAccountData) {
        if (!(updatedAccountData instanceof java.util.Map<?, ?> m)) {
            return false;
        }

        Object userIdRaw = m.get("userId");
        int userId = 0;
        if (userIdRaw instanceof Number n) {
            userId = n.intValue();
        } else if (userIdRaw != null) {
            try { userId = Integer.parseInt(String.valueOf(userIdRaw).trim()); } catch (NumberFormatException ignored) {}
        }
        if (userId <= 0) {
            return false;
        }

        if (userAccount.getViewAccount(userId) == null) {
            return false;
        }

        String username  = m.get("username")      == null ? "" : String.valueOf(m.get("username")).trim();
        String fullName  = m.get("fullName")       == null ? "" : String.valueOf(m.get("fullName")).trim();
        String email     = m.get("email")          == null ? "" : String.valueOf(m.get("email")).trim();
        String phone     = m.get("phoneNumber")    == null ? "" : String.valueOf(m.get("phoneNumber")).trim();
        String password  = m.get("password")       == null ? "" : String.valueOf(m.get("password")).trim();
        String status    = m.get("accountStatus")  == null ? "" : String.valueOf(m.get("accountStatus")).trim();
        String profileIdText = m.get("profileId") == null ? "" : String.valueOf(m.get("profileId")).trim();

        if (username.isBlank() || fullName.isBlank() || email.isBlank() || phone.isBlank()
                || password.isBlank() || status.isBlank() || profileIdText.isBlank()) {
            return false;
        }

        int profileId = 0;
        try { profileId = Integer.parseInt(profileIdText); } catch (NumberFormatException ignored) {}
        if (profileId <= 0) {
            return false;
        }

        if (userAccount.isDuplicateAccount(username, email, phone, userId)) {
            return false;
        }

        java.util.Map<String, Object> updateData = new java.util.HashMap<>();
        updateData.put("userId", userId);
        updateData.put("username", username);
        updateData.put("fullName", fullName);
        updateData.put("email", email);
        updateData.put("phoneNumber", phone);
        updateData.put("password", password);
        updateData.put("accountStatus", status);
        updateData.put("profileId", profileId);
        return userAccount.saveUpdateAccount(updateData);
    }
}
