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
            
        if (roleName == null || roleName.trim().isEmpty()) return "false";

        // Boundary handles duplicate-check logic
        List<UserProfile> allProfiles = searchController.searchProfiles(null, "all");
        for (UserProfile profile : allProfiles) {
            if (profile.getRoleName().equalsIgnoreCase(roleName.trim())) {
                return "duplicate";
            }
        }

        UserProfile newProfile = new UserProfile(roleName.trim(), status);
        return createController.createProfile(newProfile) ? "true" : "false";
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
        
        if (newRoleName == null || newRoleName.trim().isEmpty()) return "false";

        // Boundary handles duplicate-check logic (excluding self)
        List<UserProfile> allProfiles = searchController.searchProfiles(null, "all");
        for (UserProfile p : allProfiles) {
            if (p.getRoleName().equalsIgnoreCase(newRoleName.trim()) && !p.getProfileId().equals(profileID)) {
                return "duplicate";
            }
        }

        return updateController.updateProfile(profileID, newRoleName) ? "true" : "false";
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
