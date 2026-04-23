package com.uow.userprofile;

import org.springframework.stereotype.Controller;

@Controller
public class CreateUserProfileController {

    private final UserProfile userProfile = new UserProfile();

    private String errorMessage = "";

    public String getErrorMessage() { return errorMessage; }

    public boolean createProfile(String roleName, String status) {
        errorMessage = "";

        if (roleName == null || roleName.isBlank()) { errorMessage = "Role name is required."; return false; }
        if (status == null || status.isBlank()) { errorMessage = "Status is required."; return false; }
        String st = status.trim();
        if (!st.equalsIgnoreCase("Active") && !st.equalsIgnoreCase("Suspended")) {
            errorMessage = "Invalid status.";
            return false;
        }

        userProfile.roleName = roleName.trim();
        userProfile.status = st;

        if (!userProfile.saveCreateProfile()) {
            errorMessage = userProfile.lastErrorMessage;
            return false;
        }
        return true;
    }
}
