package com.uow.useraccount;

import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class CreateUserAccountController {

    private final UserAccount userAccount;

    public CreateUserAccountController() {
        this(new UserAccount());
    }

    CreateUserAccountController(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public boolean createAccount(Map<String, Object> cleanAccountData) {
        String username = String.valueOf(cleanAccountData.get("username")).trim();
        String email = String.valueOf(cleanAccountData.get("email")).trim();
        String phoneNumber = String.valueOf(cleanAccountData.get("phoneNumber")).trim();

        // Core Business Domain Rule: System duplication constraint check
        String duplicateField = userAccount.checkDuplicateField(username, email, phoneNumber, 0);
        if (duplicateField != null) {
            throw new IllegalArgumentException("Creation failed. " + duplicateField + " is already in use.");
        }

        // Domain rules validated, instruct Entity layer to proceed with data serialization (SQL save)
        return userAccount.saveCreateAccount(cleanAccountData);
    }
}