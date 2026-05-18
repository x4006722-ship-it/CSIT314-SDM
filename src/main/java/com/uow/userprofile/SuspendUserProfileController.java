package com.uow.userprofile;

import org.springframework.stereotype.Service;

@Service
public class SuspendUserProfileController {

    public boolean suspendProfile(String profileID, String action) {
        if (profileID == null || profileID.isBlank() || action == null || action.isBlank()) {
            return false;
        }
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile == null) {
            return false;
        }
        if ("suspend".equalsIgnoreCase(action.trim())) {
            return profile.updateStatus("Suspended");
        } else if ("reactivate".equalsIgnoreCase(action.trim())) {
            return profile.updateStatus("Active");
        }
        return false;
    }
}
