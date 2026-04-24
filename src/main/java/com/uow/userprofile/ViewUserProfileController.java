package com.uow.userprofile;

import org.springframework.stereotype.Service;


@Service
public class ViewUserProfileController {

    public UserProfile getProfileDetails(String profileID) {
        return UserProfile.findByID(profileID);
    }
}