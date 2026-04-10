package com.uow.boundary;

import com.uow.control.UserAdminSearchAccountController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
/**
 * Boundary (API) for searching user accounts.
 */
public class UserAdminSearchAccount {

    private final UserAdminSearchAccountController controller;

    @Autowired
    public UserAdminSearchAccount(UserAdminSearchAccountController controller) {
        this.controller = controller;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAccounts(@RequestParam(value = "query", defaultValue = "") String query,
                                           @RequestParam(value = "status", defaultValue = "") String status,
                                           @RequestParam(value = "role", defaultValue = "") String role,
                                           @RequestParam(value = "page", defaultValue = "1") int page,
                                           @RequestParam(value = "pageSize", defaultValue = "15") int pageSize) {
        try {
            int safePageSize = Math.min(100, Math.max(1, pageSize));
            int safePage = Math.max(1, page);

            List<Map<String, Object>> rows = controller.search(query, status, role, safePage, safePageSize);
            int total = controller.count(query, status, role);
            int totalPages = Math.max(1, (int) Math.ceil(total / (double) safePageSize));

            return ResponseEntity.ok(Map.of(
                    "items", rows,
                    "page", safePage,
                    "pageSize", safePageSize,
                    "total", total,
                    "totalPages", totalPages
            ));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error", "detail", safeMessage(e)));
        }
    }

    private static String safeMessage(Exception e) {
        String m = e.getMessage();
        return m == null ? "Unknown error" : m;
    }
}

