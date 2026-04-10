package com.uow.control;

import org.springframework.stereotype.Service;

import com.uow.entity.CreateUserProfile;

@Service
public class UserAdminCreateProfileController {

    /**
     * Creates a profile and saves it into the database.
     *
     * @return null on success; {@link CreateUserProfile#MSG_ROLE_ALREADY_EXISTS} or another error message on failure
     */
    public String createProfile(String roleName, String status) {
        System.out.println("Control layer logic: Creating profile object...");

        CreateUserProfile newProfile = new CreateUserProfile(null, roleName, status);
        if (newProfile.getRoleName() == null || newProfile.getRoleName().isEmpty()) {
            return "Role name is required.";
        }
        if (newProfile.existsRoleNameIgnoreCase()) {
            return CreateUserProfile.MSG_ROLE_ALREADY_EXISTS;
        }
        if (!newProfile.saveToPFDatabase()) {
            return "Database failure. Check your SQL table or connection.";
        }
        return null;
    }
}
