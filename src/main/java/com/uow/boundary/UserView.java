package com.uow.boundary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.uow.control.UserAdminViewController;
import com.uow.entity.ViewUserProfile.ProfileDTO;

@RestController 
@RequestMapping("/api/profiles")
public class UserView {

    private final UserAdminViewController controller;

    @Autowired
    public UserView(UserAdminViewController controller) {
        this.controller = controller;
    }

    @GetMapping("/view/{profileId}")
    @ResponseBody
    public String viewProfile(@PathVariable("profileId") String profileId) {
        System.out.println("Boundary received request to view profile: " + profileId);
        
        // Pass the request to the Control layer
        String profileInfo = controller.getProfile(profileId);
        
        System.out.println("Boundary returning profile display");
        return profileInfo;
    }

    @GetMapping("/list")
    @ResponseBody
    public List<ProfileDTO> listAllProfiles() {
        System.out.println("Boundary received request to list all profiles");
        
        List<ProfileDTO> profiles = controller.getAllProfiles();
        
        System.out.println("Boundary returning " + profiles.size() + " profiles");
        return profiles;
    }
}
