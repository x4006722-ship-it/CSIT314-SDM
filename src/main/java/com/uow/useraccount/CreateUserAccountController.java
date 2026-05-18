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

        String username = map.get("username") == null ? "" : String.valueOf(map.get("username")).trim();
        String password = map.get("password") == null ? "" : String.valueOf(map.get("password")).trim();
        String email    = map.get("email")    == null ? "" : String.valueOf(map.get("email")).trim();
        String phone    = map.get("phoneNumber") == null ? "" : String.valueOf(map.get("phoneNumber")).trim();
        String status   = map.get("accountStatus") == null ? "" : String.valueOf(map.get("accountStatus")).trim();
        String profileIdText = map.get("profileId") == null ? "" : String.valueOf(map.get("profileId")).trim();

        if (username.isBlank() || password.isBlank() || email.isBlank()
                || phone.isBlank() || status.isBlank() || profileIdText.isBlank()) {
            return false;
        }

        if (userAccount.isDuplicateAccount(username, email, phone, 0)) {
            return false;
        }

        return userAccount.saveCreateAccount(newAccountData);
    }
}
