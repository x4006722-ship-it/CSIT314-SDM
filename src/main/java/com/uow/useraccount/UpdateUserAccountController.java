package com.uow.useraccount;

import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class UpdateUserAccountController {

    private final UserAccount userAccount = new UserAccount();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8}$");

    public boolean updateAccount(Object updatedAccountData) {
        if (!(updatedAccountData instanceof Map<?, ?> m)) {
            throw new IllegalArgumentException("Invalid data type.");
        }

        int userId = parseInt(m.get("userId"));
        if (userId <= 0) throw new IllegalArgumentException("Invalid User ID.");

        Object current = userAccount.getViewAccount(userId);
        if (!(current instanceof Map<?, ?> currentMap)) {
            throw new IllegalArgumentException("Account not found in database.");
        }

        String username = extractString(m, "username", currentMap.get("username"));
        String fullName = extractString(m, "fullName", currentMap.get("full_name"));
        String email = extractString(m, "email", currentMap.get("email"));
        String phoneNumber = extractString(m, "phoneNumber", currentMap.get("phone_number"));
        String password = extractString(m, "password", currentMap.get("password"));
        String accountStatus = extractString(m, "accountStatus", currentMap.get("a_status"));
        int profileId = parseInt(extractString(m, "profileId", currentMap.get("profile_id")));

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || 
            phoneNumber.isEmpty() || password.isEmpty() || accountStatus.isEmpty() || profileId <= 0) {
            throw new IllegalArgumentException("Please fill in all required fields.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Phone number must be exactly 8 digits.");
        }
        if (password.length() < 3) {
            throw new IllegalArgumentException("Password must be at least 3 characters.");
        }

        if (userAccount.isDuplicateAccount(username, email, phoneNumber, userId)) {
            throw new IllegalArgumentException("Update failed. Username, Email, or Phone number is already in use.");
        }

        Map<String, Object> updateData = new java.util.HashMap<>();
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

    private String extractString(Map<?, ?> m, String key, Object fallback) {
        if (m.containsKey(key)) {
            Object val = m.get(key);
            return val == null ? "" : String.valueOf(val).trim();
        }
        return fallback == null ? "" : String.valueOf(fallback).trim();
    }

    private int parseInt(Object value) {
        if (value instanceof Number number) return number.intValue();
        if (value == null) return 0;
        try { return Integer.parseInt(String.valueOf(value).trim()); } 
        catch (NumberFormatException e) { return 0; }
    }
}
// package com.uow.useraccount;

// import org.springframework.stereotype.Controller;
// import java.util.Map;

// /**
//  * Handles user account updates with validation and duplicate checking.
//  */
// @Controller
// public class UpdateUserAccountController {

//     private final UserAccount userAccount = new UserAccount();

//     /**
//      * Updates an existing user account with new information.
//      * * @param updatedAccountData A Map containing userId and fields to update
//      * @return true if update was successful, false if validation fails
//      */
//     public boolean updateAccount(Object updatedAccountData) {
//         if (!(updatedAccountData instanceof Map<?, ?> m)) {
//             return false;
//         }

//         int userId = parseInt(m.get("userId"));
//         if (userId <= 0) {
//             return false;
//         }

//         Object current = userAccount.getViewAccount(userId);
//         if (!(current instanceof Map<?, ?> currentMap)) {
//             return false;
//         }

//         // 真实接收前端传递过来的参数内容（如果被用户删除置空，则拿到空字符串）
//         String username = extractString(m, "username", currentMap.get("username"));
//         String fullName = extractString(m, "fullName", currentMap.get("full_name"));
//         String email = extractString(m, "email", currentMap.get("email"));
//         String phoneNumber = extractString(m, "phoneNumber", currentMap.get("phone_number"));
//         String password = extractString(m, "password", currentMap.get("password"));
//         String accountStatus = extractString(m, "accountStatus", currentMap.get("a_status"));
//         int profileId = parseInt(extractString(m, "profileId", currentMap.get("profile_id")));

//         // 【核心修复】：后端核心拦截防御层，若出现非空或者非合规的格式直接截断
//         if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || 
//             phoneNumber.isEmpty() || password.isEmpty() || accountStatus.isEmpty() || profileId <= 0) {
//             return false; 
//         }
//         if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
//             return false; 
//         }
        
//         // 【核心修改】：后端限制手机号必须是正好 8 位数字
//         if (!phoneNumber.matches("^\\d{8}$")) {
//             return false; 
//         }
//         if (password.length() < 3) {
//             return false;
//         }

//         if (userAccount.isDuplicateAccount(username, email, phoneNumber, userId)) {
//             return false;
//         }

//         java.util.Map<String, Object> updateData = new java.util.HashMap<>();
//         updateData.put("userId", userId);
//         updateData.put("username", username);
//         updateData.put("fullName", fullName);
//         updateData.put("email", email);
//         updateData.put("phoneNumber", phoneNumber);
//         updateData.put("password", password);
//         updateData.put("accountStatus", accountStatus);
//         updateData.put("profileId", profileId);
        
//         return userAccount.saveUpdateAccount(updateData);
//     }

//     private String extractString(Map<?, ?> m, String key, Object fallback) {
//         if (m.containsKey(key)) {
//             Object val = m.get(key);
//             return val == null ? "" : String.valueOf(val).trim();
//         }
//         return fallback == null ? "" : String.valueOf(fallback).trim();
//     }

//     private int parseInt(Object value) {
//         if (value instanceof Number number) {
//             return number.intValue();
//         }
//         if (value == null) {
//             return 0;
//         }
//         try {
//             return Integer.parseInt(String.valueOf(value).trim());
//         } catch (NumberFormatException e) {
//             return 0;
//         }
//     }
// }