package com.uow.control;

import com.uow.entity.SuspendAccount;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
/**
 * Control layer for suspending/activating user accounts.
 */
public class UserAdminSuspendAccountController {

    private final SuspendAccount suspendAccount = new SuspendAccount();

    public String toggleAccountStatus(int userId) throws SQLException {
        return suspendAccount.toggleAccountStatus(userId);
    }
}

