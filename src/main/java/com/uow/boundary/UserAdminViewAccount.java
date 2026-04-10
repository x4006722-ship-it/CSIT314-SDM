package com.uow.boundary;

import com.uow.control.UserAdminViewAccountController;
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
 * Boundary (API) for listing and viewing user accounts.
 */
public class UserAdminViewAccount {

    private final UserAdminViewAccountController controller;

    @Autowired
    public UserAdminViewAccount(UserAdminViewAccountController controller) {
        this.controller = controller;
    }

    @GetMapping("/list")
    /** Returns a paged list for table display. */
    public ResponseEntity<?> listAccounts(@RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "pageSize", defaultValue = "15") int pageSize) {
        try {
            int safePageSize = Math.min(2000, Math.max(1, pageSize));
            int safePage = Math.max(1, page);

            List<Map<String, Object>> rows = controller.listAccounts(safePage, safePageSize);
            int total = controller.countAccounts();
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

    @GetMapping("/view")
    /** Returns full account details for a single user_id. */
    public ResponseEntity<?> viewAccount(@RequestParam("userId") int userId) {
        try {
            Map<String, Object> row = controller.viewAccount(userId);
            if (row == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Account not found", "userId", userId));
            }
            return ResponseEntity.ok(row);
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

