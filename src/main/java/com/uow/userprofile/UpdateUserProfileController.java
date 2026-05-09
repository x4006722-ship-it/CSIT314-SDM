package com.uow.userprofile;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserProfileController {
    public boolean updateProfile(String profileID, String newRoleName) {
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile == null) return false;
        return profile.updateRoleName(newRoleName);
    }
}