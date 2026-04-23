package com.uow.useraccount;

import org.springframework.stereotype.Controller;

@Controller
public class CreateUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    private String errorMessage = "";

    public String getErrorMessage() { return errorMessage; }

    public boolean createAccount(String username, String password, String fullName, String email, String phoneNumber, int profileID, String accountStatus) {
        errorMessage = "";

        String u = username == null ? "" : username.trim();
        String p = password == null ? "" : password;
        String fn = fullName == null ? "" : fullName.trim();
        String em = email == null ? "" : email.trim();
        String ph = phoneNumber == null ? "" : phoneNumber.trim();
        String ast = accountStatus == null ? "" : accountStatus.trim();

        if (u.isEmpty()) { errorMessage = "Username is required."; return false; }
        if (p.isBlank()) { errorMessage = "Password is required."; return false; }
        if (fn.isEmpty()) { errorMessage = "Full name is required."; return false; }
        if (em.isEmpty()) { errorMessage = "Email is required."; return false; }
        if (!em.contains("@")) { errorMessage = "Invalid email."; return false; }
        if (ph.isEmpty()) { errorMessage = "Phone number is required."; return false; }

        int phone;
        try {
            phone = Integer.parseInt(ph);
        } catch (NumberFormatException e) {
            errorMessage = "Phone number must be numeric.";
            return false;
        }
        if (phone < 0) { errorMessage = "Invalid phone number."; return false; }
        if (profileID <= 0) { errorMessage = "Profile is required."; return false; }
        if (ast.isEmpty()) { errorMessage = "Account status is required."; return false; }
        if (!ast.equalsIgnoreCase("Active") && !ast.equalsIgnoreCase("Suspended")) {
            errorMessage = "Invalid account status.";
            return false;
        }

        userAccount.username = u;
        userAccount.password = p;
        userAccount.fullName = fn;
        userAccount.email = em;
        userAccount.phoneNumber = ph;
        userAccount.profileID = profileID;
        userAccount.status = ast.equalsIgnoreCase("Active") ? "Active" : "Suspended";

        if (!userAccount.saveCreateAccount()) {
            errorMessage = userAccount.lastErrorMessage;
            return false;
        }
        return true;
    }
}
