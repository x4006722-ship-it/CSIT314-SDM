package com.uow.userprofile;
import org.springframework.stereotype.Service;

@Service
public class CreateUserProfileController {
    public boolean createProfile(UserProfile profile) {
        return profile.save();
    }
}