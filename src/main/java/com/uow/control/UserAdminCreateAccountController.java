package com.uow.control;

import org.springframework.stereotype.Service;

import com.uow.entity.CreateAccount;

@Service
/**
 * Control layer for creating a user account.
 */
public class UserAdminCreateAccountController {

    public String createAccount(String username,
                                String password,
                                String profileId,
                                String fullName,
                                String email,
                                String phone) {
        CreateAccount account = new CreateAccount(username, password, profileId, fullName, email, phone, "Active");
        return account.saveToAccountDatabase();
    }
}
