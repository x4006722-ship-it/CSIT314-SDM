package com.uow.userprofile;
import org.springframework.stereotype.Service;

/**
 * Retrieves user profile (role) information.
 * 
 * Responsibilities:
 * - Query profile by ID
 * - Return complete profile details
 */
@Service
public class ViewUserProfileController {
    /**
     * Retrieves a specific user profile by its ID.
     * 
     * @param profileID The profile ID to retrieve
     * @return The UserProfile object if found, null otherwise
     */
    public UserProfile getProfileDetails(String profileID) {
        return UserProfile.findByID(profileID);
    }
}