package com.uow.control;

import com.uow.entity.UpdateAccount;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
/**
 * Control layer for updating user accounts.
 */
public class UserAdminUpdateAccountController {

    private final UpdateAccount updateAccount = new UpdateAccount();

    public String updateAccount(int userId, Map<String, Object> fields) {
        return updateAccount.updateAccount(userId, fields);
    }
}

