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

        java.util.Map<String, Object> searchData = new java.util.HashMap<>();
        searchData.put("username", text(map.get("username")));
        searchData.put("email", text(map.get("email")));
        searchData.put("phoneNumber", text(map.get("phoneNumber")));

        Object duplicateRows = userAccount.getSearchAccount(searchData);
        if (duplicateRows instanceof java.util.List<?> rows && !rows.isEmpty()) {
            return false;
        }

        return userAccount.saveCreateAccount(newAccountData);
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
