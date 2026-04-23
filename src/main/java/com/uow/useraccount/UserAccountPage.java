package com.uow.useraccount;

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

import jakarta.servlet.http.HttpSession;

@Controller
public class UserAccountPage {

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

    public String showAccountPage() {
        return "forward:/ManageAccount.html";
    }

    public String showAccountSuccessMessage(String toastMessage) {
        String msg = toastMessage == null ? "" : toastMessage;
        return "redirect:/ManageAccount.html?toast=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
    }

    public String showAccountErrorMessage(String message) {
        String msg = message == null || message.isBlank() ? "" : message;
        return "redirect:/ManageAccount.html?toast=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
    }

    //Create Account
    @PostMapping(path = "/api/accounts/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onCreateAccount(@RequestParam(value = "username", defaultValue = "") String username,
                                  @RequestParam(value = "password", defaultValue = "") String password,
                                  @RequestParam(value = "fullName", defaultValue = "") String fullName,
                                  @RequestParam(value = "email", defaultValue = "") String email,
                                  @RequestParam(value = "phone", defaultValue = "") String phoneNumber,
                                  @RequestParam(value = "profileId", defaultValue = "0") int profileID,
                                  @RequestParam(value = "accountStatus", defaultValue = "") String accountStatus) {
        boolean ok = createUserAccountController.createAccount(username, password, fullName, email, phoneNumber, profileID, accountStatus);
        return ok
                ? showAccountSuccessMessage("Account created successfully.")
                : showAccountErrorMessage(createUserAccountController.getErrorMessage());
    }

    //View Account — if userId is 0 (not provided), returns the logged-in user's own account
    @GetMapping("/api/accounts/view")
    @ResponseBody
    public Object onViewAccount(@RequestParam(value = "userId", defaultValue = "0") int userID, HttpSession session) {
        if (userID <= 0) {
            Integer sessionUserId = (Integer) session.getAttribute("userId");
            if (sessionUserId == null || sessionUserId <= 0) {
                return java.util.Map.of("error", "Not logged in.");
            }
            userID = sessionUserId;
        }
        return viewUserAccountController.viewAccount(userID);
    }

    //Update Account
    @PostMapping(path = "/api/accounts/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onUpdateAccount(@RequestBody java.util.Map<String, Object> payload) {
        boolean ok = updateUserAccountController.updateAccount(payload);
        if (ok) {
            return java.util.Map.of("success", true, "message", "Account updated successfully.");
        }
        return java.util.Map.of(
                "success", false,
                "error", updateUserAccountController.getErrorMessage()
        );
    }

    //Suspend Account
    @PostMapping("/api/accounts/suspend")
    @ResponseBody
    public Object onSuspendAccount(@RequestParam(value = "userID", defaultValue = "0") int userID, HttpSession session) {
        Integer sessionUserId = (Integer) session.getAttribute("userId");
        int currentUserId = sessionUserId == null ? 0 : sessionUserId;
        boolean ok = suspendUserAccountController.suspendAccount(userID, currentUserId);
        if (ok) {
            return java.util.Map.of("success", true, "message", "Account status updated successfully.");
        }
        return java.util.Map.of(
                "success", false,
                "error", suspendUserAccountController.getErrorMessage()
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
        Object result = searchUserAccountController.searchAccount(username, fullName, email, phoneNumber, status, profileID);
        return result == null ? java.util.List.of() : result;
    }
}
