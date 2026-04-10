package com.uow.control;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uow.entity.ViewUserProfile;
import com.uow.entity.ViewUserProfile.ProfileDTO;

@Service
public class UserAdminViewController {

    // A regular Java method called by the Boundary Layer
    public String getProfile(String profileId) {
        System.out.println("Control layer logic: Retrieving profile with ID: " + profileId);

        // 1. Create the Entity object with profile ID to fetch
        ViewUserProfile profile = new ViewUserProfile(profileId);

        // 2. Command the Entity to fetch itself from the MySQL database
        ViewUserProfile fetchedProfile = profile.getFromPFDatabase();

        // 3. Format and return the profile data as a string for display
        if (fetchedProfile != null) {
            String result = "Profile ID: " + fetchedProfile.getProfileId()
                    + "\nRole Name: " + fetchedProfile.getRoleName()
                    + "\nStatus: " + fetchedProfile.getStatus();
            return result;
        } else {
            return "Profile not found";
        }
    }

    public List<ProfileDTO> getAllProfiles() {
        System.out.println("Control layer logic: Fetching all profiles");

        // Create Entity object and fetch all profiles
        ViewUserProfile entity = new ViewUserProfile(null);
        List<ProfileDTO> profiles = entity.getAllFromPFDatabase();

        return profiles;
    }
}
