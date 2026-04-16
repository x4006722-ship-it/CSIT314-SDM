package com.uow.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uow.control.UserAccountController;

@Controller
public class UserAccountUI {

    @Autowired
    private UserAccountController userAccountController;

    // Common:
    public String showAccountPage() {
        return "forward:/ManageAccount.html";
    }

    public String showAccountSuccessMessage() {
        return "redirect:/ManageAccount.html?toast=Create%20Account%20Success";
    }

    public String showAccountErrorMessage() {
        return "redirect:/ManageAccount.html?toast=Error";
    }

    // Create Account
    @PostMapping(path = "/api/accounts/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onCreateAccount(@RequestParam(value = "username", defaultValue = "") String username,
                                  @RequestParam(value = "password", defaultValue = "") String password,
                                  @RequestParam(value = "fullName", defaultValue = "") String fullName,
                                  @RequestParam(value = "email", defaultValue = "") String email,
                                  @RequestParam(value = "phone", defaultValue = "") String phoneNumber,
                                  @RequestParam(value = "profileId", defaultValue = "0") int profileID) {
        int phone = 0;
        try {
            phone = Integer.parseInt(phoneNumber == null ? "0" : phoneNumber.trim());
        } catch (Exception ignored) {
            phone = -1;
        }
        userAccountController.validateInput(username, fullName, email, phone, "", profileID);
        userAccountController.createAccount(username, password, fullName, email, phoneNumber, profileID);
        return userAccountController.ok ? showAccountSuccessMessage() : showAccountErrorMessage();
    }

    // View Account
    @GetMapping("/api/accounts/view")
    @ResponseBody
    public Object onViewAccount(@RequestParam(value = "userId", defaultValue = "0") int userID) {
        return userAccountController.viewAccount(userID);
    }
    
    // Update Account
    @PostMapping(path = "/api/accounts/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onUpdateAccount(@RequestParam(value = "userID", defaultValue = "0") int userID,
                                    @RequestParam(value = "username", defaultValue = "") String username,
                                    @RequestParam(value = "fullName", defaultValue = "") String fullName,
                                    @RequestParam(value = "email", defaultValue = "") String email,
                                    @RequestParam(value = "phoneNumber", defaultValue = "0") int phoneNumber,
                                    @RequestParam(value = "password", defaultValue = "") String password,
                                    @RequestParam(value = "status", defaultValue = "") String status,
                                    @RequestParam(value = "profileID", defaultValue = "0") int profileID) {
        userAccountController.validateInput(username, fullName, email, phoneNumber, status, profileID);
        userAccountController.updateAccount(userID, username, fullName, email, phoneNumber, password, status, profileID);
        return userAccountController.ok ? "Success" : "Error";
    }

    // Suspend Account
    @PostMapping("/api/accounts/suspend")
    @ResponseBody
    public Object onSuspendAccount(@RequestParam(value = "userID", defaultValue = "0") int userID) {
        userAccountController.suspendAccount(userID);
        if (userAccountController.ok) {
            return java.util.Map.of("success", true);
        }
        return java.util.Map.of(
                "success", false,
                "error", userAccountController.errorMessage == null ? "Suspend not allowed." : userAccountController.errorMessage
        );
    }
    
    // Search Account
    @GetMapping({"/api/accounts/search", "/api/accounts/list"})
    @ResponseBody
    public Object onSearchAccount(@RequestParam(value = "username", defaultValue = "") String username,
                                  @RequestParam(value = "fullName", defaultValue = "") String fullName,
                                  @RequestParam(value = "email", defaultValue = "") String email,
                                  @RequestParam(value = "phoneNumber", defaultValue = "0") int phoneNumber,
                                  @RequestParam(value = "status", defaultValue = "") String status,
                                  @RequestParam(value = "profileID", defaultValue = "0") int profileID) {
        userAccountController.validateInput(username, fullName, email, phoneNumber, status, profileID);
        return userAccountController.searchAccount(username, fullName, email, phoneNumber, status, profileID);
    }
}

