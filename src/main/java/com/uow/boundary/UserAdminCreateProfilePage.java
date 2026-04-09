package com.uow.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uow.control.UserAdminCreateProfileController;

@RestController 
@RequestMapping("/api/profiles")
public class UserAdminCreateProfilePage {

    private final UserAdminCreateProfileController controller;

    @Autowired
    public UserAdminCreateProfilePage(UserAdminCreateProfileController controller) {
        this.controller = controller;
    }

    @PostMapping("/create")
    public String submitForm(
            @RequestParam("roleName") String roleName, 
            @RequestParam("status") String status) {
            
        System.out.println("Boundary received request for: " + roleName);
        
        // Pass the data to the Control layer
        boolean isSuccess = controller.createProfile(roleName, status);
        
        if (isSuccess) {
            return "Success: Profile '" + roleName + "' has been saved!";
        } else {
            return "Error: Database failure. Check your SQL table or connection.";
        }
    }
}