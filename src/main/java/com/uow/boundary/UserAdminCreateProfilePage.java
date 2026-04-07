package com.uow.boundary;

import com.uow.control.UserAdminCreateProfileController;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController // Tells Spring: This Boundary handles communication with the webpage (HTML)
@RequestMapping("/api/profiles")

public class UserAdminCreateProfilePage {

    private final UserAdminCreateProfileController controller;

    // Spring automatically injects the Controller here (Dependency Injection)
    @Autowired
    public UserAdminCreateProfilePage(UserAdminCreateProfileController controller) {
        this.controller = controller;
    }

    // Receives the POST request sent from the HTML form
    @PostMapping("/create")
    public String submitForm(@RequestParam String roleName, @RequestParam String status) {
        System.out.println("Boundary received web input: Role = " + roleName + ", Status = " + status);
        
        // The Boundary makes no logical decisions; it passes data directly to the Control layer
        boolean isSuccess = controller.createProfile(roleName, status);
        
        // Based on the Control layer's result, the Boundary decides what text to display to the user
        if (isSuccess) {
            return "Profile '" + roleName + "' created successfully! Returning to Admin Dashboard...";
        } else {
            return "Error: Please try a different name or check the database connection.";
        }
    }
}