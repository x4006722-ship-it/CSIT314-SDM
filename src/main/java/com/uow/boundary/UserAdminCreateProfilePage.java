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

    @PostMapping("/create")
    public String submitForm(@RequestParam String roleName, @RequestParam String status) {
        // Boundary only handles the request and sends data to the Control layer
        boolean isSuccess = controller.createProfile(roleName, status);
        
        if (isSuccess) {
            return "Success: Profile '" + roleName + "' has been saved!";
        } else {
            // If it returns false, it means the SQL execution failed
            return "Error: Database failure. Check your SQL table or connection.";
        }
    }
}