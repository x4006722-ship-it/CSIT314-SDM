package com.uow.userprofile;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateUserProfileController {

    // Helper method specific to creation
    private Boolean validateProfileData(String roleName) {
        List<UserProfile> allProfiles = UserProfile.findAll(null, "all");
        for (UserProfile profile : allProfiles) {
            if (profile.getRoleName().equalsIgnoreCase(roleName.trim())) {
                return false;
            }
        }
        return true; 
    }

    public String createProfile(String roleName, String status) {
        if (roleName == null || roleName.trim().isEmpty()) {
            return "Role name cannot be empty";
        }
        if (!validateProfileData(roleName)) {
            return "Role already exists";
        }

        UserProfile newProfile = new UserProfile(roleName.trim(), status);
        Boolean success = newProfile.save();
        return success ? "Success" : "Database Error";
    }
}
