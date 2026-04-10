package com.uow.control;

import org.springframework.stereotype.Service;

import com.uow.entity.UserProfile;

@Service 
/**
 * Control layer for creating a new role/profile.
 */
public class UserAdminCreateProfileController {

    /** Creates a profile and saves it into the database. */
    public boolean createProfile(String roleName, String status) {
        UserProfile newProfile = new UserProfile(null, roleName, status);
        return newProfile.saveToPFDatabase();
    }
}