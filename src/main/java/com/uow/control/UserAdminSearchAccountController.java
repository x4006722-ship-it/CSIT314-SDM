package com.uow.control;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.uow.entity.SearchAccount;

/**
 * Control layer for searching user accounts (paged).
 */
@Service
public class UserAdminSearchAccountController {

    private final SearchAccount searchAccount = new SearchAccount();

    /** Delegates search rules to the entity layer. */
    public List<Map<String, Object>> search(String query,
                                            String statusFilter,
                                            String roleFilter,
                                            int page,
                                            int pageSize) throws SQLException {
        return searchAccount.search(query, statusFilter, roleFilter, page, pageSize);
    }

    /** Returns total count for the same search criteria. */
    public int count(String query, String statusFilter, String roleFilter) throws SQLException {
        return searchAccount.count(query, statusFilter, roleFilter);
    }
}

