package com.uow.useraccount;

import java.util.HashMap;
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

    @Autowired private CreateUserAccountController createUserAccountController;
    @Autowired private ViewUserAccountController viewUserAccountController;
    @Autowired private UpdateUserAccountController updateUserAccountController;
    @Autowired private SuspendUserAccountController suspendUserAccountController;
    @Autowired private SearchUserAccountController searchUserAccountController;

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
        if (password.length() < 3) {
            uiMessage = "Password too short.";
            return false;
        }
        if (phoneNumber.length() < 8) {
            uiMessage = "Phone number too short.";
            return false;
        }

        boolean result = createUserAccountController.createAccount(newAccountData);
        uiMessage = result ? "Account created successfully." : "Account create failed.";
        return result;
    }

    @GetMapping("/api/accounts/view")
    @ResponseBody
    public Object onViewAccount(
            @RequestParam(value = "userId", required = false) Integer userId,
            HttpSession session) {
        Object body = viewUserAccountController.viewAccount(userId, session);
        if (body == null) {
            return Map.of("error", "Not signed in or account not found.");
        }
        return body;
    }

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
        String password = readText(raw.get("password"));
        if (!password.isBlank() && password.length() < 3) {
            uiMessage = "Password too short.";
            return false;
        }
        if (!phoneNumber.isBlank() && phoneNumber.length() < 8) {
            uiMessage = "Phone number too short.";
            return false;
        }

        boolean result = updateUserAccountController.updateAccount(updatedAccountData);
        uiMessage = result ? "Account updated successfully." : "Account update failed.";
        return result;
    }

    @PostMapping("/api/accounts/suspend")
    @ResponseBody
    public boolean onSuspendAccount(@RequestParam("targetUserId") int targetUserId, HttpSession session) {
        if (targetUserId <= 0) {
            uiMessage = "Invalid target user ID.";
            return false;
        }
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

    @GetMapping("/api/accounts/search")
    @ResponseBody
    public Object onSearchAccount(@RequestParam Map<String, String> params) {
        Map<String, Object> searchData = new HashMap<>();
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

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
