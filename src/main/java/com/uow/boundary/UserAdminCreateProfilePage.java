package com.uow.boundary;

import com.uow.control.UserAdminCreateProfileController;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController 
@RequestMapping("/api/profiles")
public class UserAdminCreateProfilePage {

    private final UserAdminCreateProfileController controller;

    @Autowired
    public UserAdminCreateProfilePage(UserAdminCreateProfileController controller) {
        this.controller = controller;
    }

    // ==========================================
    // THE FIX IS HERE: Add ("roleName") and ("status") 
    // ==========================================
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