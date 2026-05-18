package com.uow.userprofile;
import org.springframework.stereotype.Service;

/**
 * Handles creation of new user roles/profiles.
 * 
 * Responsibilities:
 * - Validate profile data (role name and status)
 * - Check for duplicate role names
 * - Persist new profile to database
 */
@Service
public class CreateUserProfileController {
    
    /**
     * Creates a new user profile (role) with validation.
     * 
     * @param profile The UserProfile object with role name and status
     * @return true if profile was successfully created
     * @throws IllegalArgumentException if role name is a duplicate
     */
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