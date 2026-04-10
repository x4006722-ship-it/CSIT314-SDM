package com.uow.boundary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uow.control.UserAdminCreateProfileController;
import com.uow.entity.CreateUserProfile;
import com.uow.entity.ViewUserProfile;
import com.uow.entity.ViewUserProfile.ProfileDTO;

@RestController
@RequestMapping("/api/profiles")
/**
 * Boundary (API) for listing and creating user profiles (roles).
 */
public class UserAdminCreateProfilePage {

    private final UserAdminCreateProfileController controller;

    @Autowired
    public UserAdminCreateProfilePage(UserAdminCreateProfileController controller) {
        this.controller = controller;
    }

    @GetMapping("/list")
    public List<ProfileDTO> listProfiles() {
        return new ViewUserProfile(null).getAllFromPFDatabase();
    }

    @PostMapping("/create")
    public String submitForm(
            @RequestParam("roleName") String roleName,
            @RequestParam("status") String status) {

        System.out.println("Boundary received request for: " + roleName);
        System.out.println("Received parameters - roleName: " + roleName + ", status: " + status);

        try {
            String err = controller.createProfile(roleName, status);

            if (err == null) {
                System.out.println("Profile creation successful");
                return "Success: Profile '" + roleName + "' has been saved!";
            }
            if (CreateUserProfile.MSG_ROLE_ALREADY_EXISTS.equals(err)) {
                return CreateUserProfile.MSG_ROLE_ALREADY_EXISTS;
            }
            System.out.println("Profile creation failed: " + err);
            return "Error: " + err;
        } catch (Exception e) {
            System.err.println("Exception during profile creation: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
