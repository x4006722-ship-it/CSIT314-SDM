package com.uow.userprofile;

import org.springframework.stereotype.Service;

@Service
public class CreateUserProfileController {

    public boolean createProfile(String roleName, String status) throws IllegalArgumentException {
        if (roleName == null || roleName.isBlank() || status == null || status.isBlank()) {
            return false;
        }
        if (UserProfile.isDuplicateRole(roleName, null)) {
            throw new IllegalArgumentException("duplicate");
        }
        return new UserProfile(roleName.trim(), status.trim()).save();
    }
}
