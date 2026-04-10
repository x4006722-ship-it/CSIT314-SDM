package com.uow.boundary;

import com.uow.control.UserAdminUpdateAccountController;
import com.uow.entity.SuspendAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
/**
 * Boundary (API) for updating user account fields.
 * Expects JSON payload with userId and a fields map.
 */
public class UserAdminUpdateAccount {

    private final UserAdminUpdateAccountController controller;

    @Autowired
    public UserAdminUpdateAccount(UserAdminUpdateAccountController controller) {
        this.controller = controller;
    }

    @PostMapping("/update")
    /** Updates editable account fields (e.g., username, email, role, status). */
    public ResponseEntity<?> updateAccount(@RequestBody Map<String, Object> payload) {
        try {
            int userId = parseInt(payload.get("userId"));
            @SuppressWarnings("unchecked")
            Map<String, Object> fields = payload.get("fields") instanceof Map
                    ? (Map<String, Object>) payload.get("fields")
                    : new HashMap<>();

            String error = controller.updateAccount(userId, fields);
            if (error == null) return ResponseEntity.ok(Map.of("success", true));
            if (SuspendAccount.MSG_CANNOT_SUSPEND_ACCOUNT.equals(error)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "error", error));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "error", error));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Server error", "detail", safeMessage(e)));
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

