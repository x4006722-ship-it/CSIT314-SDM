package com.uow.control;

import org.springframework.stereotype.Service;

import com.uow.entity.UserProfile;

@Service 
public class UserAdminCreateProfileController {

    // A regular Java method called by the Boundary Layer
    public boolean createProfile(String roleName, String status) {
        System.out.println("Control layer logic: Creating profile object...");
        
        // 1. Create the Entity object
        UserProfile newProfile = new UserProfile(null, roleName, status);
        
        // 2. Command the Entity to save itself to the MySQL database
        return newProfile.saveToPFDatabase();
    }
}