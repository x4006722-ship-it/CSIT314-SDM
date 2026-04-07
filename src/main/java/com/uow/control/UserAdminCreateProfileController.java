package com.uow.control;

import com.uow.entity.UserProfile; 
import org.springframework.stereotype.Service;

@Service // Tells Spring this is the "Brain" (Control layer logic)
public class UserAdminCreateProfileController {

    // This is a regular Java method called by the Boundary
    public boolean createProfile(String roleName, String status) {
        
        System.out.println("Control layer processing logic for: " + roleName);
        
        // 1. Create the Entity object
        UserProfile newProfile = new UserProfile(null, roleName, status);
        
        // 2. Command the Entity to save itself to the database
        return newProfile.saveToDatabase();
    }
}