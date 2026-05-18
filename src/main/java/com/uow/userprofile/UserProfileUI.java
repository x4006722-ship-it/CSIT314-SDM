package com.uow.userprofile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(originPatterns = "*")
public class UserProfileUI {

    @Autowired private CreateUserProfileController createController;
    @Autowired private ViewUserProfileController viewController;
    @Autowired private SearchUserProfileController searchController;
    @Autowired private UpdateUserProfileController updateController;
    @Autowired private SuspendUserProfileController suspendController;

    public String showProfilePage() {
        return "forward:/ManageProfile.html";
    }

    public String showProfileSuccessMessage(String message) {
        return message == null || message.isBlank() ? "Operation successful." : message;
    }

    public String showProfileErrorMessage(String message) {
        return message == null || message.isBlank() ? "Operation failed." : message;
    }

    @PostMapping("/create")
    public String onCreateProfile(
            @RequestParam("roleName") String roleName,
            @RequestParam("status") String status) {
        if (roleName == null || roleName.isBlank() || status == null || status.isBlank()) {
            return showProfileErrorMessage("Empty field detected.");
        }
        try {
            boolean success = createController.createProfile(roleName, status);
            return success ? "true" : "false";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @GetMapping("/view/{profileID}")
    public UserProfile onViewProfile(@PathVariable("profileID") String profileID) {
        return viewController.getProfileDetails(profileID);
    }

    @GetMapping({"/list", "/search"})
    public List<UserProfile> onSearchProfile(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {
        return searchController.searchProfiles(keyword, status);
    }

    @PostMapping("/update-role/{profileID}")
    public String onUpdateProfile(
            @PathVariable("profileID") String profileID,
            @RequestParam("newRoleName") String newRoleName) {
        if (profileID == null || profileID.isBlank() || newRoleName == null || newRoleName.isBlank()) {
            return showProfileErrorMessage("Empty field detected.");
        }
        try {
            boolean success = updateController.updateProfile(profileID, newRoleName);
            return success ? "true" : "false";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @PostMapping({"/suspend/{profileID}", "/reactivate/{profileID}"})
    public String onSuspendProfile(
            @PathVariable("profileID") String profileID,
            @RequestParam("action") String action) {
        if (profileID == null || profileID.isBlank()) {
            return showProfileErrorMessage("Invalid profile ID.");
        }
        return suspendController.suspendProfile(profileID, action) ? "true" : "false";
    }
}
