package com.uow.control;

import org.springframework.stereotype.Service;
import com.uow.entity.UserProfile;
import java.util.List;

@Service
public class UserProfileController {

    // Private helper method to prevent duplicate roles
    private Boolean validateProfileData(String roleName) {
        List<UserProfile> allProfiles = UserProfile.findAll(null, "all");
        for (UserProfile profile : allProfiles) {
            if (profile.getRoleName().equalsIgnoreCase(roleName.trim())) {
                return false; // Validation fails if role name already exists
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

    public UserProfile getProfileDetails(String profileID) {
        return UserProfile.findByID(profileID);
    }

    public List<UserProfile> searchProfiles(String keyword, String status) {
        return UserProfile.findAll(keyword, status);
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