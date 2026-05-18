package com.uow.useraccount;

import org.springframework.stereotype.Controller;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UpdateUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    public boolean updateAccount(int userId, Map<String, Object> cleanAccountData) {
        // Business Domain Rule 1: Targeted record must exist in the persistent store
        Object current = userAccount.getViewAccount(userId);
        if (!(current instanceof Map<?, ?> currentMap)) {
            throw new IllegalArgumentException("Account not found in database.");
        }

        // Business Logic Processing: Implement dynamic fallback data merge mechanics
        String username = extractString(cleanAccountData, "username", currentMap.get("username"));
        String fullName = extractString(cleanAccountData, "fullName", currentMap.get("full_name"));
        String email = extractString(cleanAccountData, "email", currentMap.get("email"));
        String phoneNumber = extractString(cleanAccountData, "phoneNumber", currentMap.get("phone_number"));
        String password = extractString(cleanAccountData, "password", currentMap.get("password"));
        String accountStatus = extractString(cleanAccountData, "accountStatus", currentMap.get("a_status"));
        
        Object profileIdObj = cleanAccountData.get("profileId");
        int profileId = profileIdObj != null && !String.valueOf(profileIdObj).isBlank() 
                        ? Integer.parseInt(String.valueOf(profileIdObj)) 
                        : (Integer) currentMap.get("profile_id");

        // Business Domain Rule 2: Validation on data collisions/uniqueness (excluding current record identifier)
        String duplicateField = userAccount.checkDuplicateField(username, email, phoneNumber, userId);
        if (duplicateField != null) {
            throw new IllegalArgumentException("Update failed. " + duplicateField + " is already in use.");
        }

        // Compile payload mapping structural architecture for Entity-level operations
        Map<String, Object> updateData = new HashMap<>();
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

    private String extractString(Map<String, Object> m, String key, Object fallback) {
        Object val = m.get(key);
        if (val != null && !String.valueOf(val).trim().isEmpty()) {
            return String.valueOf(val).trim();
        }
        return fallback == null ? "" : String.valueOf(fallback).trim();
    }
}
