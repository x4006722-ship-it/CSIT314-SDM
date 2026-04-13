package com.uow.control;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.uow.entity.ViewAccount;

/**
 * Control layer for reading user accounts.
 */
@Service
public class UserAdminViewAccountController {

    private final ViewAccount viewAccount = new ViewAccount();

    public List<Map<String, Object>> listAccounts(int page, int pageSize) throws SQLException {
        return viewAccount.fetchAccountSummaries(page, pageSize);
    }

    public int countAccounts() throws SQLException {
        return viewAccount.fetchAccountCount();
    }

    public Map<String, Object> viewAccount(int userId) throws SQLException {
        return viewAccount.fetchAccountDetail(userId);
    }
}

