package com.uow.userprofile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(originPatterns = "*") 
public class UserProfileUI {

    private final CreateUserProfileController createController;
    private final ViewUserProfileController viewController;
    private final SearchUserProfileController searchController;
    private final UpdateUserProfileController updateController;
    private final SuspendUserProfileController suspendController;

    @Autowired
    public UserProfileUI(
            CreateUserProfileController createController,
            ViewUserProfileController viewController,
            SearchUserProfileController searchController,
            UpdateUserProfileController updateController,
            SuspendUserProfileController suspendController) {
        this.createController = createController;
        this.viewController = viewController;
        this.searchController = searchController;
        this.updateController = updateController;
        this.suspendController = suspendController;
    }

    @PostMapping("/create")
    public String onCreateProfile(
            @RequestParam("roleName") String roleName,
            @RequestParam("status") String status) {
            
        UserProfile newProfile = new UserProfile(roleName, status);
        try {
            boolean success = createController.createProfile(newProfile);
            return success ? "true" : "false";
        } catch (IllegalArgumentException e) {
            return e.getMessage(); //  "duplicate"
        }
    }

    @GetMapping("/view/{profileID}")
    public UserProfile onViewProfileClick(@PathVariable("profileID") String profileID) {
        return viewController.getProfileDetails(profileID);
    }

    @GetMapping({"/list", "/search"})
    public List<UserProfile> onSearchInput(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {
        return searchController.searchProfiles(keyword, status);
    }

    @PostMapping("/update-role/{profileID}")
    public String onUpdateProfileClick(
            @PathVariable("profileID") String profileID,
            @RequestParam("newRoleName") String newRoleName) {
            
        try {
            boolean success = updateController.updateProfile(profileID, newRoleName);
            return success ? "true" : "false";
        } catch (IllegalArgumentException e) {
            return e.getMessage(); //  "duplicate"
        }
    }

    @PostMapping("/suspend/{profileID}")
    public String onSuspendProfileClick(@PathVariable("profileID") String profileID) {
        return suspendController.suspendProfile(profileID) ? "true" : "false";
    }

    @PostMapping("/reactivate/{profileID}")
    public String onReactivateProfileClick(@PathVariable("profileID") String profileID) {
        return suspendController.reactivateProfile(profileID) ? "true" : "false";
    }
}