package com.uow.userprofile;

import org.springframework.stereotype.Controller;

@Controller
public class ViewUserProfileController {

    private final UserProfile userProfile = new UserProfile();

    public Object viewProfile(int profileID) {
        return userProfile.getViewProfile(profileID);
    }
}
