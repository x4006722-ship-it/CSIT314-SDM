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

        // 边界验证：各字段不能为空
        if (username.isBlank() || password.isBlank() || email.isBlank() || phoneNumber.isBlank()) {
            return false;
        }

        // 边界验证：邮箱格式
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }

        // 边界验证：电话号码长度（最少8位）
        if (phoneNumber.length() < 8) {
            return false;
        }

        // 边界验证：密码长度（最少3位）
        if (password.length() < 3) {
            return false;
        }

        // 【核心修复】：使用严格的去重方法，0代表不排除任何人
        if (userAccount.isDuplicateAccount(username, email, phoneNumber, 0)) {
            return false;
        }

        return userAccount.saveCreateAccount(newAccountData);
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
