package com.uow.useraccount;

import org.springframework.stereotype.Controller;

@Controller
public class UpdateUserAccountController {

    private final UserAccount userAccount = new UserAccount();

    private String errorMessage = "";

    public String getErrorMessage() { return errorMessage; }

    public boolean updateAccount(java.util.Map<String, Object> payload) {
        errorMessage = "";

        int userID = toInt(payload == null ? null : payload.get("userId"));
        if (userID <= 0) { errorMessage = "Invalid userId."; return false; }

        // Extract incoming fields from payload
        java.util.Map<String, Object> incoming = new java.util.HashMap<>();
        Object f = payload == null ? null : payload.get("fields");
        if (f instanceof java.util.Map<?, ?> m) {
            for (var e : m.entrySet()) {
                if (e.getKey() != null) incoming.put(String.valueOf(e.getKey()), e.getValue());
            }
        }

        // Load existing account data from DB, then override with incoming non-blank values
        java.util.Map<String, Object> merged = new java.util.HashMap<>();
        Object view = userAccount.getViewAccount(userID);
        if (view instanceof java.util.Map<?, ?> m) {
            for (var e : m.entrySet()) {
                if (e.getKey() != null) merged.put(String.valueOf(e.getKey()), e.getValue());
            }
        }
        for (var e : incoming.entrySet()) {
            String v = e.getValue() == null ? "" : String.valueOf(e.getValue()).trim();
            if (!v.isEmpty() && !v.equals("-")) merged.put(e.getKey(), e.getValue());
        }

        String email  = str(merged, "email");
        String status = str(merged, "a_status");
        int phoneNumber = toInt(merged.get("phone_number"));
        int profileID   = toInt(merged.get("profile_id"));

        if (!email.isEmpty() && !email.contains("@")) { errorMessage = "Invalid email."; return false; }
        if (phoneNumber < 0) { errorMessage = "Invalid phone number."; return false; }
        if (profileID < 0)   { errorMessage = "Invalid profile."; return false; }
        if (!status.isEmpty() && !status.equalsIgnoreCase("Active") && !status.equalsIgnoreCase("Suspended")) {
            errorMessage = "Invalid status."; return false;
        }

        userAccount.userID       = userID;
        userAccount.username     = str(merged, "username");
        userAccount.fullName     = str(merged, "full_name");
        userAccount.email        = email;
        userAccount.phoneNumber  = String.valueOf(phoneNumber);
        userAccount.password     = str(merged, "password");
        userAccount.status       = status;
        userAccount.profileID    = profileID;

        if (!userAccount.saveUpdateAccount(userID)) {
            errorMessage = userAccount.lastErrorMessage;
            return false;
        }
        return true;
    }

    private String str(java.util.Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? "" : String.valueOf(v).trim();
    }

    private int toInt(Object v) {
        if (v instanceof Number n) return n.intValue();
        if (v == null) return 0;
        try { return Integer.parseInt(String.valueOf(v).trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
