package com.uow.control;

import com.uow.entity.ViewAccount;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
/**
 * Control layer for reading user accounts.
 */
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

