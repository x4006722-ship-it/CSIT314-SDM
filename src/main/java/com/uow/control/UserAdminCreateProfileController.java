package com.uow.control;

import org.springframework.stereotype.Service;

import com.uow.entity.CreateUserProfile;

@Service
public class UserAdminCreateProfileController {

    /** Creates a profile and saves it into the database. */
    public boolean createProfile(String roleName, String status) {
        System.out.println("Control layer logic: Creating profile object...");

        CreateUserProfile newProfile = new CreateUserProfile(null, roleName, status);

        return newProfile.saveToPFDatabase();
    }
}
