package com.uow.userprofile;

import org.springframework.stereotype.Controller;

@Controller
public class UpdateUserProfileController {

    private final UserProfile userProfile = new UserProfile();

    private String errorMessage = "";

    public String getErrorMessage() { return errorMessage; }

    public boolean updateProfile(int profileID, String roleName, String status) {
        errorMessage = "";

        if (profileID <= 0) { errorMessage = "Invalid profile."; return false; }
        if (roleName == null || roleName.isBlank()) { errorMessage = "Role name is required."; return false; }

        String st = status == null ? "" : status.trim();
        if (!st.isEmpty() && !st.equalsIgnoreCase("Active") && !st.equalsIgnoreCase("Suspended")) {
            errorMessage = "Invalid status.";
            return false;
        }

        // Merge with existing if status not provided
        if (st.isEmpty()) {
            Object existing = userProfile.getViewProfile(profileID);
            if (existing instanceof java.util.Map<?, ?> m) {
                Object s = m.get("status");
                st = s == null ? "" : String.valueOf(s);
            }
        }

        userProfile.roleName = roleName.trim();
        userProfile.status = st;

        if (!userProfile.saveUpdateProfile(profileID)) {
            errorMessage = userProfile.lastErrorMessage;
            return false;
        }
        return true;
    }
}
