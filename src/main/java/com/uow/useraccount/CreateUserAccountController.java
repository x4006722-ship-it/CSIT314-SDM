package com.uow.useraccount;

import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class CreateUserAccountController {

    private final UserAccount userAccount;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8}$");

    public CreateUserAccountController() {
        this(new UserAccount());
    }

    CreateUserAccountController(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public boolean createAccount(Object newAccountData) {
        if (!(newAccountData instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Invalid data type.");
        }

        String username = text(map.get("username"));
        String password = text(map.get("password"));
        String fullName = text(map.get("fullName"));
        String email = text(map.get("email"));
        String phoneNumber = text(map.get("phoneNumber"));
        String accountStatus = text(map.get("accountStatus"));
        String profileIdText = text(map.get("profileId"));

        // 校验失败直接抛异常，边界类会捕获
        if (username.isBlank() || password.isBlank() || fullName.isBlank() || email.isBlank() 
                || phoneNumber.isBlank() || accountStatus.isBlank() || profileIdText.isBlank()) {
            throw new IllegalArgumentException("Please fill in all required fields.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) { 
            throw new IllegalArgumentException("Phone number must be exactly 8 digits.");
        }
        if (password.length() < 3) {
            throw new IllegalArgumentException("Password must be at least 3 characters.");
        }

        // 查重防御 (0代表不排除任何ID)
        if (userAccount.isDuplicateAccount(username, email, phoneNumber, 0)) {
            throw new IllegalArgumentException("Creation failed. Username, Email, or Phone number is already in use.");
        }

        return userAccount.saveCreateAccount(newAccountData);
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
// package com.uow.useraccount;

// import org.springframework.stereotype.Controller;

// /**
//  * Handles user account creation with comprehensive validation.
//  * 
//  * Responsibilities:
//  * - Validate account data (username, email, phone, password format and length)
//  * - Check for duplicate usernames, emails, and phone numbers
//  * - Enforce business rules (email format, phone length, password minimum)
//  * - Delegate to UserAccount entity for database insertion
//  */
// @Controller
// public class CreateUserAccountController {

//     private final UserAccount userAccount;

//     public CreateUserAccountController() {
//         this(new UserAccount());
//     }

//     CreateUserAccountController(UserAccount userAccount) {
//         this.userAccount = userAccount;
//     }

//     /**
//      * Creates a new user account with full validation.
//      * 
//      * Validates:
//      * - All required fields are not empty
//      * - Email format matches pattern
//      * - Phone number is at least 8 digits
//      * - Password is at least 3 characters
//      * - No duplicate username, email, or phone exists
//      * 
//      * @param newAccountData A Map containing username, password, fullName, email, phoneNumber, accountStatus, profileId
//      * @return true if account created successfully, false if validation fails
//      */
//     public boolean createAccount(Object newAccountData) {
//         if (!(newAccountData instanceof java.util.Map<?, ?> map)) {
//             return false;
//         }

//         String username = text(map.get("username"));
//         String password = text(map.get("password"));
//         String email = text(map.get("email"));
//         String phoneNumber = text(map.get("phoneNumber"));
//         String accountStatus = text(map.get("accountStatus"));
//         String profileIdText = text(map.get("profileId"));

//         // 完整的边界验证
//         if (username.isBlank() || password.isBlank() || email.isBlank() || phoneNumber.isBlank() 
//                 || accountStatus.isBlank() || profileIdText.isBlank()) {
//             return false;
//         }

//         // 边界验证：邮箱格式
//         if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
//             return false;
//         }

//         // 边界验证：电话号码长度（最少8位）
//         if (!phoneNumber.matches("^\\d{8}$")) { 
//         return false;
//         }

//         // 边界验证：密码长度（最少3位）
//         if (password.length() < 3) {
//             return false;
//         }

//         // 【核心修复】：使用严格的去重方法，0代表不排除任何人
//         if (userAccount.isDuplicateAccount(username, email, phoneNumber, 0)) {
//             return false;
//         }

//         return userAccount.saveCreateAccount(newAccountData);
//     }

//     private String text(Object value) {
//         return value == null ? "" : String.valueOf(value).trim();
//     }
// }
