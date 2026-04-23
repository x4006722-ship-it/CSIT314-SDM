package com.uow.userprofile;

import org.springframework.stereotype.Controller;

@Controller
public class SearchUserProfileController {

    private final UserProfile userProfile = new UserProfile();

    public Object searchProfile(String keyword, String status) {
        return userProfile.getSearchProfile(keyword, status);
    }
}
