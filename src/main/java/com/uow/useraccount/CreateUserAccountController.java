package com.uow.useraccount;

import org.springframework.stereotype.Controller;

@Controller
public class CreateUserAccountController {

    private final UserAccount userAccount;

    public CreateUserAccountController() {
        this(new UserAccount());
    }

    CreateUserAccountController(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public boolean createAccount(Object newAccountData) {
        if (!(newAccountData instanceof java.util.Map<?, ?> map)) {
            return false;
        }

        String username = text(map.get("username"));
        String password = text(map.get("password"));
        String email = text(map.get("email"));
        String phoneNumber = text(map.get("phoneNumber"));
        String accountStatus = text(map.get("accountStatus"));
        String profileIdText = text(map.get("profileId"));

        // Full boundary validation
        if (username.isBlank() || password.isBlank() || email.isBlank() || phoneNumber.isBlank() 
                || accountStatus.isBlank() || profileIdText.isBlank()) {
            return false;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }

        // Validate phone number length (minimum 8 digits)
        if (phoneNumber.length() < 8) {
            return false;
        }

        // Validate password length (minimum 3 characters)
        if (password.length() < 3) {
            return false;
        }

        // Strict duplicate check — 0 means no account is excluded
        if (userAccount.isDuplicateAccount(username, email, phoneNumber, 0)) {
            return false;
        }

        return userAccount.saveCreateAccount(newAccountData);
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
