package com.uow.userprofile;
import org.springframework.stereotype.Service;

@Service
public class CreateUserProfileController {
    
    public boolean createProfile(UserProfile profile) throws IllegalArgumentException {
        
        // rolename and status check 
        if (profile == null || 
            profile.getRoleName() == null || profile.getRoleName().trim().isEmpty() ||
            profile.getStatus() == null || profile.getStatus().trim().isEmpty()) { 
            return false;
        }

        // duplicate check
        if (UserProfile.isDuplicateRole(profile.getRoleName(), null)) {
            throw new IllegalArgumentException("duplicate"); 
        }

        // return boolean
        return profile.save();
    }
}