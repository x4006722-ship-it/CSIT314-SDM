package com.uow.useraccount;

import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserAccountPage {

    @Autowired private CreateUserAccountController createUserAccountController;
    @Autowired private ViewUserAccountController viewUserAccountController;
    @Autowired private UpdateUserAccountController updateUserAccountController;
    @Autowired private SuspendUserAccountController suspendUserAccountController;
    @Autowired private SearchUserAccountController searchUserAccountController;

    // RegEx validation logic shifted to the Boundary layer
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8}$");

    public String showAccountPage() {
        return "forward:/ManageAccount.html";
    }
 

    @PostMapping("/api/accounts/create")
    @ResponseBody
    public Map<String, Object> onCreateAccount(@RequestBody Map<String, Object> newAccountData) {
        try {
            // Boundary Guard: Clean and validate basic formats
            String username = text(newAccountData.get("username"));
            String password = text(newAccountData.get("password"));
            String fullName = text(newAccountData.get("fullName"));
            String email = text(newAccountData.get("email"));
            String phoneNumber = text(newAccountData.get("phoneNumber"));
            String accountStatus = text(newAccountData.get("accountStatus"));
            String profileIdText = text(newAccountData.get("profileId"));

            if (username.isBlank() || password.isBlank() || fullName.isBlank() || email.isBlank() 
                    || phoneNumber.isBlank() || accountStatus.isBlank() || profileIdText.isBlank()) {
                return Map.of("success", false, "message", "Please fill in all required fields.");
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                return Map.of("success", false, "message", "Invalid email format.");
            }
            if (!PHONE_PATTERN.matcher(phoneNumber).matches()) { 
                return Map.of("success", false, "message", "Phone number must be exactly 8 digits.");
            }
            if (password.length() < 3) {
                return Map.of("success", false, "message", "Password must be at least 3 characters.");
            }

            // Delegate sanitized data to Controller
            boolean result = createUserAccountController.createAccount(newAccountData);
            if (result) return Map.of("success", true, "message", "Account created successfully.");
            return Map.of("success", false, "message", "Database error occurred.");

        } catch (IllegalArgumentException e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @PostMapping("/api/accounts/update")
    @ResponseBody
    public Map<String, Object> onUpdateAccount(@RequestBody Map<String, Object> updatedAccountData) {
        try {
            // Boundary Guard: Filter incoming request map structure
            int userId = parseInt(updatedAccountData.get("userId"));
            if (userId <= 0) return Map.of("success", false, "message", "Invalid User ID.");

            String email = text(updatedAccountData.get("email"));
            String phoneNumber = text(updatedAccountData.get("phoneNumber"));
            String password = text(updatedAccountData.get("password"));

            if (!email.isBlank() && !EMAIL_PATTERN.matcher(email).matches()) {
                return Map.of("success", false, "message", "Invalid email format.");
            }
            if (!phoneNumber.isBlank() && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
                return Map.of("success", false, "message", "Phone number must be exactly 8 digits.");
            }
            if (!password.isBlank() && password.length() < 3) {
                return Map.of("success", false, "message", "Password must be at least 3 characters.");
            }

            // Delegate to business layer
            boolean result = updateUserAccountController.updateAccount(userId, updatedAccountData);
            if (result) return Map.of("success", true, "message", "Account updated successfully.");
            return Map.of("success", false, "message", "Database error occurred.");

        } catch (IllegalArgumentException e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @PostMapping("/api/accounts/check-duplicate")
    @ResponseBody
    public Map<String, Object> onCheckDuplicate(@RequestBody Map<String, Object> data) {
        try {
            String username = text(data.get("username"));
            String email = text(data.get("email"));
            String phoneNumber = text(data.get("phoneNumber"));
            int excludeUserId = parseInt(data.get("excludeUserId"));
            
            UserAccount ua = new UserAccount();
            String duplicateField = ua.checkDuplicateField(username, email, phoneNumber, excludeUserId);
            
            if (duplicateField != null) {
                return Map.of("success", false, "message", duplicateField + " is already in use.");
            }
            return Map.of("success", true, "message", "No duplicates found.");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Validation error: " + e.getMessage());
        }
    }

    @PostMapping("/api/accounts/suspend")
    @ResponseBody
    public Map<String, Object> onSuspendAccount(@RequestParam("targetUserId") int targetUserId, HttpSession session) {
        Object sidObj = session.getAttribute("userId");
        if (sidObj == null) return Map.of("success", false, "message", "User not logged in.");
        
        int currentUserId;
        try {
            currentUserId = Integer.parseInt(String.valueOf(sidObj));
        } catch (NumberFormatException e) {
            return Map.of("success", false, "message", "Invalid session identity.");
        }

        try {
            boolean result = suspendUserAccountController.suspendAccount(targetUserId, currentUserId);
            if (result) return Map.of("success", true, "message", "Account status updated.");
            return Map.of("success", false, "message", "Status change failed. Target may be User Admin.");
        } catch (IllegalArgumentException e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @GetMapping("/api/accounts/view")
    @ResponseBody
    public Object onViewAccount(@RequestParam(value = "userId", required = false) Integer userId, HttpSession session) {
        if (userId == null || userId <= 0) {
            Object sessionUserId = session.getAttribute("userId");
            if (sessionUserId instanceof Integer) userId = (Integer) sessionUserId;
            else if (sessionUserId instanceof String) {
                try { userId = Integer.parseInt((String) sessionUserId); } 
                catch (NumberFormatException e) { return Map.of("error", "User not logged in."); }
            } else return Map.of("error", "User not logged in.");
        }
        if (userId <= 0) return Map.of("error", "Empty field detected.");
        return viewUserAccountController.viewAccount(userId);
    }

    @GetMapping("/api/accounts/list")
    @ResponseBody
    public Object onListAccount() {
        java.util.Map<String, Object> searchData = new java.util.HashMap<>();
        searchData.put("username", ""); searchData.put("fullName", "");
        searchData.put("email", ""); searchData.put("phoneNumber", "");
        searchData.put("status", ""); searchData.put("profileID", 0);
        return searchUserAccountController.searchAccount(searchData);
    }

    @GetMapping("/api/accounts/search")
    @ResponseBody
    public Object onSearchAccountGet(@RequestParam Map<String, String> params) {
        java.util.Map<String, Object> searchData = new java.util.HashMap<>();
        searchData.put("username", params.get("username"));
        searchData.put("fullName", params.get("fullName"));
        searchData.put("email", params.get("email"));
        searchData.put("phoneNumber", "0".equals(params.get("phoneNumber")) ? "" : params.get("phoneNumber"));
        searchData.put("status", params.get("status"));
        searchData.put("profileID", params.get("profileID"));
        return searchUserAccountController.searchAccount(searchData);
    }

    @PostMapping("/api/accounts/search")
    @ResponseBody
    public Object onSearchAccount(@RequestBody Object searchData) {
        return searchUserAccountController.searchAccount(searchData);
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int parseInt(Object value) {
        if (value instanceof Number number) return number.intValue();
        if (value == null) return 0;
        try { return Integer.parseInt(String.valueOf(value).trim()); } 
        catch (NumberFormatException e) { return 0; }
    }
}