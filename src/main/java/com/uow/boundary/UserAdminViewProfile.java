package com.uow.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.uow.control.UserAdminViewController;

@RestController
@RequestMapping("/api/profiles")
public class UserAdminViewProfile {

    private final UserAdminViewController controller;

    @Autowired
    public UserAdminViewProfile(UserAdminViewController controller) {
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
}
