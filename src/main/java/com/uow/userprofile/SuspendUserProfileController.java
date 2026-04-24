package com.uow.userprofile;
import org.springframework.stereotype.Service;


@Service
public class SuspendUserProfileController {

    public String suspendProfile(String profileID) {
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile == null) {
            return "Profile not found";
        }

        profile.updateStatus("Suspended");
        return "Success";
    }

    public String reactivateProfile(String profileID) {
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile != null) {
            profile.updateStatus("Active");
        }
        return "Success";
    }
}
