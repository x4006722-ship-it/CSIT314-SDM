package com.uow.boundary;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uow.control.UserAccountController;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserAccountUI {

    @Autowired
    private UserAccountController userAccountController;

    //Common methods for User Account
    public String showAccountPage() {
        return "forward:/ManageAccount.html";
    }

    public String showAccountSuccessMessage(String toastMessage) {
        String msg = toastMessage == null ? "" : toastMessage;
        return "redirect:/ManageAccount.html?toast=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
    }

    public String showAccountErrorMessage(String message) {
        String msg;
        if (message == null || message.isBlank()) {
            String err = userAccountController.errorMessage;
            msg = err == null || err.isBlank() ? "" : err;
        } else {
            msg = message;
        }
        return "redirect:/ManageAccount.html?toast=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
    }
    //Common methods for User Account


    //Create Account
    @PostMapping(path = "/api/accounts/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onCreateAccount(@RequestParam(value = "username", defaultValue = "") String username,
                                  @RequestParam(value = "password", defaultValue = "") String password,
                                  @RequestParam(value = "fullName", defaultValue = "") String fullName,
                                  @RequestParam(value = "email", defaultValue = "") String email,
                                  @RequestParam(value = "phone", defaultValue = "") String phoneNumber,
                                  @RequestParam(value = "profileId", defaultValue = "0") int profileID,
                                  @RequestParam(value = "accountStatus", defaultValue = "") String accountStatus) {
        userAccountController.createAccount(username, password, fullName, email, phoneNumber, profileID, accountStatus);
        return userAccountController.ok ? showAccountSuccessMessage("Account created successfully.") : showAccountErrorMessage(null);
    }

    //View Account
    @GetMapping("/api/accounts/view")
    @ResponseBody
    public Object onViewAccount(@RequestParam(value = "userId", defaultValue = "0") int userID) {
        return userAccountController.viewAccount(userID);
    }

    //Update Account
    @PostMapping(path = "/api/accounts/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onUpdateAccount(@RequestBody java.util.Map<String, Object> payload) {
        userAccountController.updateAccount(payload);
        if (userAccountController.ok) {
            return java.util.Map.of("success", true, "message", "Account updated successfully.");
        }
        return java.util.Map.of(
                "success", false,
                "error", userAccountController.errorMessage == null ? "Update failed." : userAccountController.errorMessage
        );
    }

    //Suspend Account
    @PostMapping("/api/accounts/suspend")
    @ResponseBody
    public Object onSuspendAccount(@RequestParam(value = "userID", defaultValue = "0") int userID, HttpSession session) {
        Integer sessionUserId = (Integer) session.getAttribute("userId");
        int currentUserId = sessionUserId == null ? 0 : sessionUserId;
        userAccountController.suspendAccount(userID, currentUserId);
        if (userAccountController.ok) {
            return java.util.Map.of("success", true, "message", "Account status updated successfully.");
        }
        return java.util.Map.of(
                "success", false,
                "error", userAccountController.errorMessage == null ? "Suspend not allowed." : userAccountController.errorMessage
        );
    }

    //Search Account
    @GetMapping({"/api/accounts/search", "/api/accounts/list"})
    @ResponseBody
    public Object onSearchAccount(@RequestParam(value = "username", defaultValue = "") String username,
                                  @RequestParam(value = "fullName", defaultValue = "") String fullName,
                                  @RequestParam(value = "email", defaultValue = "") String email,
                                  @RequestParam(value = "phoneNumber", defaultValue = "0") int phoneNumber,
                                  @RequestParam(value = "status", defaultValue = "") String status,
                                  @RequestParam(value = "profileID", defaultValue = "0") int profileID) {
        Object result = userAccountController.searchAccount(username, fullName, email, phoneNumber, status, profileID);
        return result == null ? java.util.List.of() : result;
    }
}
