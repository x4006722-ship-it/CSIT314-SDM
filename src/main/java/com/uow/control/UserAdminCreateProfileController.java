package com.uow.control;

import com.uow.entity.UserProfile;
import java.util.ArrayList;
import java.util.List;

public class UserAdminCreateProfileController {
    private List<UserProfile> profileDatabase;

    public UserAdminCreateProfileController() {
        this.profileDatabase = new ArrayList<>();
        this.profileDatabase.add(new UserProfile("P001", "Admin", "Active"));
    }

    public boolean checkRoleExists(String roleName) {
        for (UserProfile profile : profileDatabase) {
            if (profile.getRoleName().equalsIgnoreCase(roleName)) {
                return true; 
            }
        }
        return false;
    }

    public boolean createProfile(String roleName, String status) {
        if (checkRoleExists(roleName)) {
            System.out.println("❌ Error: The role '" + roleName + "' already exists!");
            return false;
        }

        String newId = "P00" + (profileDatabase.size() + 1);
        UserProfile newProfile = new UserProfile(newId, roleName, status);
        profileDatabase.add(newProfile);
        
        System.out.println("✅ Success: Role '" + roleName + "' created with ID: " + newId);
        return true;
    }
}