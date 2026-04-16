package com.uow.control;

import org.springframework.stereotype.Controller;

import com.uow.entity.UserAccount;

@Controller
public class UserAccountController {

    private final UserAccount userAccount = new UserAccount();

    public boolean ok = false;
    public String errorMessage = "";

    // Common
    public boolean validateInput(String username, String fullName, String email, int phoneNumber, String status, int profileID) {
        errorMessage = "";

        String u = username == null ? "" : username.trim();
        String fn = fullName == null ? "" : fullName.trim();
        String em = email == null ? "" : email.trim();
        String st = status == null ? "" : status.trim();

        if (profileID < 0) {
            ok = false;
            errorMessage = "Invalid profile.";
            return false;
        }
        if (phoneNumber < 0) {
            ok = false;
            errorMessage = "Invalid phone number.";
            return false;
        }
        if (!em.isEmpty() && !em.contains("@")) {
            ok = false;
            errorMessage = "Invalid email.";
            return false;
        }
        if (!st.isEmpty()
                && !st.equalsIgnoreCase("Active")
                && !st.equalsIgnoreCase("Suspended")) {
            ok = false;
            errorMessage = "Invalid status.";
            return false;
        }

        boolean any =
                !u.isEmpty()
                        || !fn.isEmpty()
                        || !em.isEmpty()
                        || phoneNumber > 0
                        || !st.isEmpty()
                        || profileID > 0;

        if (!any) {
            ok = true;
            return true;
        }

        if (u.isEmpty() && fn.isEmpty() && em.isEmpty() && phoneNumber <= 0 && profileID <= 0 && !st.isEmpty()) {
            ok = true;
            return true;
        }

        if (profileID == 0 && (u.isEmpty() && fn.isEmpty() && em.isEmpty()) && phoneNumber <= 0 && st.isEmpty()) {
            ok = true;
            return true;
        }

        if (profileID == 0 && !u.isEmpty() && u.isBlank()) {
            ok = false;
            errorMessage = "Username is required.";
            return false;
        }

        ok = true;
        return true;
    }

    // Create Account
    public void createAccount(String username, String password, String fullName, String email, String phoneNumber, int profileID) {
        userAccount.username = username;
        userAccount.password = password;
        userAccount.fullName = fullName;
        userAccount.email = email;
        userAccount.phoneNumber = phoneNumber;
        userAccount.profileID = profileID;

        userAccount.userID = 0;
        if (!userAccount.checkDuplicate(username, email, phoneNumber)) {
            ok = false;
            return;
        }
        ok = userAccount.saveCreateAccount();
    }

    // View Account
    public Object viewAccount(int userID) {
        return userAccount.getViewAccount(userID);
    }

    // Update Account
    public void updateAccount(int userID, String username, String fullName, String email, int phoneNumber, String password, String status, int profileID) {
        userAccount.userID = userID;
        userAccount.username = username;
        userAccount.fullName = fullName;
        userAccount.email = email;
        userAccount.phoneNumber = String.valueOf(phoneNumber);
        userAccount.password = password;
        userAccount.status = status;
        userAccount.profileID = profileID;

        if (!userAccount.checkDuplicate(username, email, String.valueOf(phoneNumber))) {
            ok = false;
            return;
        }
        ok = userAccount.saveUpdateAccount(userID);
    }

    // Suspend Account
    public void suspendAccount(int userID) {
        errorMessage = "";

        Object detail = userAccount.getViewAccount(userID);
        if (detail instanceof java.util.Map<?, ?> m) {
            Object role = m.get("roleName");
            if (role != null && "User Admin".equalsIgnoreCase(String.valueOf(role).trim())) {
                ok = false;
                errorMessage = "Cannot suspend User Admin account.";
                return;
            }
        }

        ok = userAccount.saveSuspendAccount(userID);
        if (!ok) {
            errorMessage = "Suspend failed.";
        }
    }

    // Search Account
    public Object searchAccount(String username, String fullName, String email, int phoneNumber, String status, int profileID) {
        return userAccount.getSearchAccount(username, fullName, email, phoneNumber, status, profileID);
    }
}

