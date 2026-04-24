package com.uow.userprofile;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchUserProfileController {

    public List<UserProfile> searchProfiles(String keyword, String status) {
        return UserProfile.findAll(keyword, status);
    }
}
