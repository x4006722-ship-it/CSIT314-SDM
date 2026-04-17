package com.uow.control;

import org.springframework.stereotype.Controller;

import com.uow.entity.UserAccount;

@Controller
public class UserAccountController {

    private final UserAccount userAccount = new UserAccount();

    public boolean ok = false;
    public String errorMessage = "";

    private void clearFeedback() {
        errorMessage = "";
        ok = false;
    }

    //Create Account
    public void createAccount(String username, String password, String fullName, String email, String phoneNumber, int profileID, String accountStatus) {
        clearFeedback();

        String u = username == null ? "" : username.trim();
        String p = password == null ? "" : password;
        String fn = fullName == null ? "" : fullName.trim();
        String em = email == null ? "" : email.trim();
        String ph = phoneNumber == null ? "" : phoneNumber.trim();
        String ast = accountStatus == null ? "" : accountStatus.trim();

        if (u.isEmpty()) {
            errorMessage = "Username is required.";
            return;
        }
        if (p.isBlank()) {
            errorMessage = "Password is required.";
            return;
        }
        if (fn.isEmpty()) {
            errorMessage = "Full name is required.";
            return;
        }
        if (em.isEmpty()) {
            errorMessage = "Email is required.";
            return;
        }
        if (!em.contains("@")) {
            errorMessage = "Invalid email.";
            return;
        }
        if (ph.isEmpty()) {
            errorMessage = "Phone number is required.";
            return;
        }
        int phone;
        try {
            phone = Integer.parseInt(ph);
        } catch (NumberFormatException e) {
            errorMessage = "Phone number must be numeric.";
            return;
        }
        if (phone < 0) {
            errorMessage = "Invalid phone number.";
            return;
        }
        if (profileID <= 0) {
            errorMessage = "Profile is required.";
            return;
        }
        if (ast.isEmpty()) {
            errorMessage = "Account status is required.";
            return;
        }
        if (!ast.equalsIgnoreCase("Active") && !ast.equalsIgnoreCase("Suspended")) {
            errorMessage = "Invalid account status.";
            return;
        }

        userAccount.username = u;
        userAccount.password = p;
        userAccount.fullName = fn;
        userAccount.email = em;
        userAccount.phoneNumber = ph;
        userAccount.profileID = profileID;
        userAccount.status = ast.equalsIgnoreCase("Active") ? "Active" : "Suspended";

        ok = userAccount.saveCreateAccount();
        if (!ok) {
            String le = userAccount.lastErrorMessage;
            errorMessage = le == null || le.isBlank() ? "Could not create account." : le;
        }
    }

    //View Account
    public Object viewAccount(int userID) {
        return userAccount.getViewAccount(userID);
    }

    //Update Account
    public void updateAccount(java.util.Map<String, Object> payload) {
        clearFeedback();

        int userID = 0;
        try {
            Object uid = payload == null ? null : payload.get("userId");
            if (uid instanceof Number n) {
                userID = n.intValue();
            } else if (uid != null) {
                userID = Integer.parseInt(String.valueOf(uid));
            }
        } catch (Exception ignored) {
            userID = 0;
        }
        if (userID <= 0) {
            errorMessage = "Invalid userId.";
            return;
        }

        java.util.Map<String, Object> fields = java.util.Map.of();
        Object f = payload == null ? null : payload.get("fields");
        if (f instanceof java.util.Map<?, ?> m) {
            java.util.Map<String, Object> tmp = new java.util.HashMap<>();
            for (var e : m.entrySet()) {
                if (e.getKey() == null) {
                    continue;
                }
                tmp.put(String.valueOf(e.getKey()), e.getValue());
            }
            fields = tmp;
        }

        String username = String.valueOf(fields.getOrDefault("username", ""));
        String fullName = String.valueOf(fields.getOrDefault("full_name", fields.getOrDefault("fullName", "")));
        String email = String.valueOf(fields.getOrDefault("email", ""));
        String password = String.valueOf(fields.getOrDefault("password", ""));
        String status = String.valueOf(fields.getOrDefault("a_status", fields.getOrDefault("status", "")));

        int phoneNumber = 0;
        try {
            Object pn = fields.get("phone_number");
            if (pn == null) {
                pn = fields.get("phoneNumber");
            }
            if (pn instanceof Number n) {
                phoneNumber = n.intValue();
            } else if (pn != null && !String.valueOf(pn).isBlank()) {
                phoneNumber = Integer.parseInt(String.valueOf(pn));
            }
        } catch (Exception ignored) {
            phoneNumber = -1;
        }

        int profileID = 0;
        try {
            Object pid = fields.get("profile_id");
            if (pid == null) {
                pid = fields.get("profileID");
            }
            if (pid instanceof Number n) {
                profileID = n.intValue();
            } else if (pid != null && !String.valueOf(pid).isBlank()) {
                profileID = Integer.parseInt(String.valueOf(pid));
            }
        } catch (Exception ignored) {
            profileID = 0;
        }

        final java.util.Map<String, Object> existing = new java.util.HashMap<>();
        Object view = userAccount.getViewAccount(userID);
        if (view instanceof java.util.Map<?, ?> m) {
            for (var e : m.entrySet()) {
                if (e.getKey() == null) {
                    continue;
                }
                existing.put(String.valueOf(e.getKey()), e.getValue());
            }
        }

        java.util.function.Function<String, String> exStr = (k) -> {
            Object v = existing.get(k);
            return v == null ? "" : String.valueOf(v);
        };

        String mergedUsername = (username == null || username.isBlank()) ? exStr.apply("username") : username;
        String mergedFullName = (fullName == null || fullName.isBlank()) ? exStr.apply("full_name") : fullName;
        String mergedEmail = (email == null || email.isBlank()) ? exStr.apply("email") : email;
        String mergedPassword = (password == null || password.isBlank()) ? exStr.apply("password") : password;
        String mergedStatus = (status == null || status.isBlank()) ? exStr.apply("a_status") : status;

        int mergedPhoneNumber = phoneNumber;
        if (mergedPhoneNumber <= 0) {
            try {
                String p = exStr.apply("phone_number");
                mergedPhoneNumber = p == null || p.isBlank() ? 0 : Integer.parseInt(p.trim());
            } catch (Exception ignored) {
                mergedPhoneNumber = 0;
            }
        }

        int mergedProfileID = profileID;
        if (mergedProfileID <= 0) {
            try {
                String p = exStr.apply("profile_id");
                mergedProfileID = p == null || p.isBlank() ? 0 : Integer.parseInt(p.trim());
            } catch (Exception ignored) {
                mergedProfileID = 0;
            }
        }

        String em = mergedEmail == null ? "" : mergedEmail.trim();
        String st = mergedStatus == null ? "" : mergedStatus.trim();
        if (mergedProfileID < 0) {
            errorMessage = "Invalid profile.";
            return;
        }
        if (mergedPhoneNumber < 0) {
            errorMessage = "Invalid phone number.";
            return;
        }
        if (!em.isEmpty() && !em.contains("@")) {
            errorMessage = "Invalid email.";
            return;
        }
        if (!st.isEmpty()
                && !st.equalsIgnoreCase("Active")
                && !st.equalsIgnoreCase("Suspended")) {
            errorMessage = "Invalid status.";
            return;
        }

        userAccount.userID = userID;
        userAccount.username = mergedUsername;
        userAccount.fullName = mergedFullName;
        userAccount.email = mergedEmail;
        userAccount.phoneNumber = String.valueOf(mergedPhoneNumber);
        userAccount.password = mergedPassword;
        userAccount.status = mergedStatus;
        userAccount.profileID = mergedProfileID;

        ok = userAccount.saveUpdateAccount(userID);
        if (!ok) {
            String le = userAccount.lastErrorMessage;
            errorMessage = le == null || le.isBlank() ? "Update failed." : le;
        }
    }

    //Suspend Account
    public void suspendAccount(int targetUserId, int currentUserId) {
        clearFeedback();
        ok = userAccount.saveSuspendAccount(targetUserId, currentUserId);
        if (!ok) {
            String le = userAccount.lastErrorMessage;
            errorMessage = le == null || le.isBlank() ? "Suspend failed." : le;
        }
    }

    //Search Account
    public Object searchAccount(String username, String fullName, String email, int phoneNumber, String status, int profileID) {
        return userAccount.getSearchAccount(username, fullName, email, phoneNumber, status, profileID);
    }
}
