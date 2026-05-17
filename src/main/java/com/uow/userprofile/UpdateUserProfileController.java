package com.uow.userprofile;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserProfileController {
    
    // Returns boolean
    public boolean updateProfile(String profileID, String newRoleName) throws IllegalArgumentException {
        // 1. Defensive null/blank guard
        if (newRoleName == null || newRoleName.trim().isEmpty() || profileID == null) {
            return false;
        }

        // 2. Business rule: duplicate role name check (excluding this profile)
        if (UserProfile.isDuplicateRole(newRoleName, profileID)) {
            // Business rule violation — throw exception
            throw new IllegalArgumentException("duplicate");
        }

        // 3. Validation passed — delegate update to Entity
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile == null) return false;
        
        return profile.updateRoleName(newRoleName.trim());
    }
}