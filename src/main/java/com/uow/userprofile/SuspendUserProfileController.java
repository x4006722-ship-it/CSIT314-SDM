package com.uow.userprofile;

import org.springframework.stereotype.Controller;

@Controller
public class SuspendUserProfileController {

    private final UserProfile userProfile = new UserProfile();

    private String errorMessage = "";

    public String getErrorMessage() { return errorMessage; }

    public boolean suspendProfile(int profileID) {
        errorMessage = "";

        if (profileID <= 0) { errorMessage = "Invalid profile."; return false; }

        if (!userProfile.saveSuspendProfile(profileID)) {
            errorMessage = userProfile.lastErrorMessage;
            return false;
        }
        return true;
    }
}
