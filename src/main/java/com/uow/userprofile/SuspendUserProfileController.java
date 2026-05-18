package com.uow.userprofile;
import org.springframework.stereotype.Service;

/**
 * Handles suspension and reactivation of user profiles.
 * 
 * Responsibilities:
 * - Toggle profile status between Active and Suspended
 * - Validate profile existence
 * - Support both suspension and reactivation operations
 */
@Service
public class SuspendUserProfileController {
    /**
     * Suspends a user profile by setting its status to "Suspended".
     * 
     * @param profileID The profile ID to suspend
     * @return true if suspension was successful, false if profile not found
     */
    public boolean suspendProfile(String profileID) {
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile == null) return false;
        return profile.updateStatus("Suspended");
    }

    /**
     * Reactivates a suspended user profile by setting its status to "Active".
     * 
     * @param profileID The profile ID to reactivate
     * @return true if reactivation was successful, false if profile not found
     */
    public boolean reactivateProfile(String profileID) {
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile == null) return false;
        return profile.updateStatus("Active");
    }
}