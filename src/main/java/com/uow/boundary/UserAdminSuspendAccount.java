package com.uow.boundary;

import com.uow.control.UserAdminSuspendAccountController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
/**
 * Boundary (API) for toggling account status between Active and Suspended.
 */
public class UserAdminSuspendAccount {

    private final UserAdminSuspendAccountController controller;

    @Autowired
    public UserAdminSuspendAccount(UserAdminSuspendAccountController controller) {
        this.controller = controller;
    }

    @PostMapping("/suspend")
    /** Toggles a_status for a user_id and returns the new status. */
    public ResponseEntity<?> toggleSuspend(@RequestBody Map<String, Object> payload) {
        int userId = parseInt(payload.get("userId"));
        if (userId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", "Invalid userId"));
        }

        try {
            String newStatus = controller.toggleAccountStatus(userId);
            if (newStatus == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Account not found"));
            }
            return ResponseEntity.ok(Map.of("success", true, "accountStatus", newStatus));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Database error", "detail", safeMessage(e)));
        }
    }

    private static int parseInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(String.valueOf(o));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String safeMessage(Exception e) {
        String m = e.getMessage();
        return m == null ? "Unknown error" : m;
    }
}

