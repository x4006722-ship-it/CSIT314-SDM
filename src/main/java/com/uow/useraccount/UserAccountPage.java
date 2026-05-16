package com.uow.useraccount;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserAccountPage {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern DIGITS_PATTERN = Pattern.compile("^\\d+$");

    @Autowired
    private CreateUserAccountController createUserAccountController;

    @Autowired
    private ViewUserAccountController viewUserAccountController;

    @Autowired
    private UpdateUserAccountController updateUserAccountController;

    @Autowired
    private SuspendUserAccountController suspendUserAccountController;

    @Autowired
    private SearchUserAccountController searchUserAccountController;

    private String uiMessage = "";

    public String showAccountPage() {
        return "forward:/ManageAccount.html";
    }

    public String showAccountSuccessMessage() {
        return uiMessage.isBlank() ? "Operation successful." : uiMessage;
    }

    public String showAccountErrorMessage() {
        return uiMessage.isBlank() ? "Operation failed." : uiMessage;
    }

    //Create Account
    @PostMapping("/api/accounts/create")
    @ResponseBody
    public boolean onCreateAccount(@RequestBody Object newAccountData) {
        if (!(newAccountData instanceof Map<?, ?> raw)) {
            uiMessage = "Invalid data type.";
            return false;
        }

        String username = readText(raw.get("username"));
        String password = readText(raw.get("password"));
        String fullName = readText(raw.get("fullName"));
        String email = readText(raw.get("email"));
        String phoneNumber = readText(raw.get("phoneNumber"));
        String accountStatus = readText(raw.get("accountStatus"));
        String profileIdText = readText(raw.get("profileId"));

        if (username.isBlank() || password.isBlank() || fullName.isBlank()
                || email.isBlank() || phoneNumber.isBlank()
                || accountStatus.isBlank() || profileIdText.isBlank()) {
            uiMessage = "Empty field detected.";
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            uiMessage = "Invalid email format.";
            return false;
        }
        if (!DIGITS_PATTERN.matcher(phoneNumber).matches() || !DIGITS_PATTERN.matcher(profileIdText).matches()) {
            uiMessage = "Type mismatch detected.";
            return false;
        }

        boolean result = createUserAccountController.createAccount(newAccountData);
        uiMessage = result ? "Account created successfully." : "Account create failed.";
        return result;
    }

    //View Account
    @GetMapping("/api/accounts/view")
    @ResponseBody
    public Object onViewAccount(@RequestParam(value = "userId", required = false) Integer userId, HttpSession session) {
        // 如果未提供 userId，从 Session 中获取当前登录用户的 userId
        if (userId == null || userId <= 0) {
            Object sessionUserId = session.getAttribute("userId");
            if (sessionUserId instanceof Integer) {
                userId = (Integer) sessionUserId;
            } else if (sessionUserId instanceof String) {
                try {
                    userId = Integer.parseInt((String) sessionUserId);
                } catch (NumberFormatException e) {
                    return Map.of("error", "User not logged in.");
                }
            } else {
                return Map.of("error", "User not logged in.");
            }
        }
        if (userId <= 0) {
            return Map.of("error", "Empty field detected.");
        }
        return viewUserAccountController.viewAccount(userId);
    }

    //Update Account
    @PostMapping("/api/accounts/update")
    @ResponseBody
    public boolean onUpdateAccount(@RequestBody Object updatedAccountData) {
        if (!(updatedAccountData instanceof Map<?, ?> raw)) {
            uiMessage = "Invalid data type.";
            return false;
        }
        String userIdText = readText(raw.get("userId"));
        if (userIdText.isBlank() || !DIGITS_PATTERN.matcher(userIdText).matches()) {
            uiMessage = "Type mismatch detected.";
            return false;
        }

        String email = readText(raw.get("email"));
        if (!email.isBlank() && !EMAIL_PATTERN.matcher(email).matches()) {
            uiMessage = "Invalid email format.";
            return false;
        }
        String phoneNumber = readText(raw.get("phoneNumber"));
        if (!phoneNumber.isBlank() && !DIGITS_PATTERN.matcher(phoneNumber).matches()) {
            uiMessage = "Type mismatch detected.";
            return false;
        }
        String profileIdText = readText(raw.get("profileId"));
        if (!profileIdText.isBlank() && !DIGITS_PATTERN.matcher(profileIdText).matches()) {
            uiMessage = "Type mismatch detected.";
            return false;
        }

        boolean result = updateUserAccountController.updateAccount(updatedAccountData);
        uiMessage = result ? "Account updated successfully." : "Account update failed.";
        return result;
    }

    // //Suspend Account
    // @PostMapping("/api/accounts/suspend")
    // @ResponseBody
    // public boolean onSuspendAccount(@RequestParam("targetUserId") int targetUserId,
    //                                 @RequestParam("currentUserId") int currentUserId) {
    //     if (targetUserId <= 0 || currentUserId <= 0) {
    //         uiMessage = "Empty field detected.";
    //         return false;
    //     }
    //     boolean result = suspendUserAccountController.suspendAccount(targetUserId, currentUserId);
    //     uiMessage = result ? "Account suspend status changed." : "Account suspend failed.";
    //     return result;
    // }
    //Suspend Account
    @PostMapping("/api/accounts/suspend")
    @ResponseBody
    public boolean onSuspendAccount(@RequestParam("targetUserId") int targetUserId, HttpSession session) {
        if (targetUserId <= 0) {
            uiMessage = "Invalid target user ID.";
            return false;
        }
        
        // 【关键修复】：直接从 Session 读取当前操作人 ID，拒绝前端伪造
        Object sidObj = session.getAttribute("userId");
        if (sidObj == null) {
            uiMessage = "User not logged in.";
            return false;
        }
        
        int currentUserId;
        try {
            currentUserId = Integer.parseInt(String.valueOf(sidObj));
        } catch (NumberFormatException e) {
            return false;
        }

        boolean result = suspendUserAccountController.suspendAccount(targetUserId, currentUserId);
        uiMessage = result ? "Account suspend status changed." : "Account suspend failed.";
        return result;
    }

    //Search Account
    @GetMapping("/api/accounts/list")
    @ResponseBody
    public Object onListAccount() {
        java.util.Map<String, Object> searchData = new java.util.HashMap<>();
        searchData.put("username", "");
        searchData.put("fullName", "");
        searchData.put("email", "");
        searchData.put("phoneNumber", "");
        searchData.put("status", "");
        searchData.put("profileID", 0);
        return searchUserAccountController.searchAccount(searchData);
    }

    @GetMapping("/api/accounts/search")
    @ResponseBody
    public Object onSearchAccountGet(@RequestParam Map<String, String> params) {
        java.util.Map<String, Object> searchData = new java.util.HashMap<>();
        searchData.put("username", readText(params.get("username")));
        searchData.put("fullName", readText(params.get("fullName")));
        searchData.put("email", readText(params.get("email")));
        String phoneNumber = readText(params.get("phoneNumber"));
        searchData.put("phoneNumber", "0".equals(phoneNumber) ? "" : phoneNumber);
        searchData.put("status", readText(params.get("status")));
        searchData.put("profileID", readText(params.get("profileID")));

        String email = readText(searchData.get("email"));
        if (!email.isBlank() && !EMAIL_PATTERN.matcher(email).matches()) {
            return Map.of("error", "Invalid email format.");
        }
        String normalizedPhone = readText(searchData.get("phoneNumber"));
        if (!normalizedPhone.isBlank() && !DIGITS_PATTERN.matcher(normalizedPhone).matches()) {
            return Map.of("error", "Type mismatch detected.");
        }
        String profileIdText = readText(searchData.get("profileID"));
        if (!profileIdText.isBlank() && !DIGITS_PATTERN.matcher(profileIdText).matches()) {
            return Map.of("error", "Type mismatch detected.");
        }

        return searchUserAccountController.searchAccount(searchData);
    }

    @PostMapping("/api/accounts/search")
    @ResponseBody
    public Object onSearchAccount(@RequestBody Object searchData) {
        if (!(searchData instanceof Map<?, ?> raw)) {
            return Map.of("error", "Invalid data type.");
        }

        String email = readText(raw.get("email"));
        if (!email.isBlank() && !EMAIL_PATTERN.matcher(email).matches()) {
            return Map.of("error", "Invalid email format.");
        }
        String phoneNumber = readText(raw.get("phoneNumber"));
        if (!phoneNumber.isBlank() && !DIGITS_PATTERN.matcher(phoneNumber).matches()) {
            return Map.of("error", "Type mismatch detected.");
        }
        String profileIdText = readText(raw.get("profileID"));
        if (!profileIdText.isBlank() && !DIGITS_PATTERN.matcher(profileIdText).matches()) {
            return Map.of("error", "Type mismatch detected.");
        }

        return searchUserAccountController.searchAccount(searchData);
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
