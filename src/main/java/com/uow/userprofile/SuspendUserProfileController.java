package com.uow.userprofile;
import org.springframework.stereotype.Service;

@Service
public class SuspendUserProfileController {
    public boolean suspendProfile(String profileID) {
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile == null) return false;
        return profile.updateStatus("Suspended");
    }

    public boolean reactivateProfile(String profileID) {
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile == null) return false;
        return profile.updateStatus("Active");
    }
}