package com.uow.userprofile;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateUserProfileController {

    // Helper method specific to updating
    private Boolean validateProfileData(String roleName) {
        List<UserProfile> allProfiles = UserProfile.findAll(null, "all");
        for (UserProfile profile : allProfiles) {
            if (profile.getRoleName().equalsIgnoreCase(roleName.trim())) {
                return false;
            }
        }
        return true; 
    }

    public String updateProfile(String profileID, String newRoleName) {
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile == null) {
            return "Profile not found";
        }
        if (!validateProfileData(newRoleName)) {
            return "Role already exists";
        }

        profile.updateRoleName(newRoleName.trim());
        return "Success";
    }
}