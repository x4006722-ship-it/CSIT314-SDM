package com.uow.userprofile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "*") 
public class UserProfileUI {

    // Declare the 5 specialized controllers
    private final CreateUserProfileController createController;
    private final ViewUserProfileController viewController;
    private final SearchUserProfileController searchController;
    private final UpdateUserProfileController updateController;
    private final SuspendUserProfileController suspendController;

    // Inject all controllers via constructor
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

    // 1. Create Profile -> Uses Create Controller
    @PostMapping("/create")
    public ResponseEntity<String> onCreateProfile(
            @RequestParam("roleName") String roleName,
            @RequestParam("status") String status) {
            
        String result = createController.createProfile(roleName, status);
        if (result.equals("Success")) {
            showSuccessMessage("Profile '" + roleName + "' created successfully!");
            return ResponseEntity.ok("Success: Profile created");
        } else {
            showErrorMessage(result);
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 2. View Profile Details -> Uses View Controller
    @GetMapping("/view/{profileID}")
    public ResponseEntity<UserProfile> onViewProfileClick(@PathVariable("profileID") String profileID) {
        UserProfile profile = viewController.getProfileDetails(profileID);
        if (profile != null) {
            openProfileDrawer(profile.getRoleName(), profile.getStatus());
            return ResponseEntity.ok(profile);
        }
        showErrorMessage("Profile ID " + profileID + " not found!");
        return ResponseEntity.notFound().build();
    }

    // 3. Search and List Profiles -> Uses Search Controller
    @GetMapping({"/list", "/search"})
    public ResponseEntity<List<UserProfile>> onSearchInput(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {
        
        List<UserProfile> profiles = searchController.searchProfiles(keyword, status);
        renderTable(profiles);
        return ResponseEntity.ok(profiles);
    }

    // 4. Update Profile Role Name -> Uses Update Controller
    @PostMapping("/update-role/{profileID}")
    public ResponseEntity<String> onUpdateProfileClick(
            @PathVariable("profileID") String profileID,
            @RequestParam("newRoleName") String newRoleName) {
        
        String result = updateController.updateProfile(profileID, newRoleName);
        if (result.equals("Success")) {
            showSuccessMessage("Role updated to '" + newRoleName + "'!");
            return ResponseEntity.ok("Success");
        }
        showErrorMessage(result);
        return ResponseEntity.badRequest().body(result);
    }

    // 5. Suspend Profile -> Uses Suspend Controller
    @PostMapping("/suspend/{profileID}")
    public ResponseEntity<String> onSuspendProfileClick(@PathVariable("profileID") String profileID) {
        String result = suspendController.suspendProfile(profileID);
        if (result.equals("Success")) {
            showSuccessMessage("Profile suspended successfully!");
            return ResponseEntity.ok("Success");
        }
        showErrorMessage(result);
        return ResponseEntity.badRequest().body(result);
    }

    // 6. Reactivate Profile -> Uses Suspend Controller
    @PostMapping("/reactivate/{profileID}")
    public ResponseEntity<String> onReactivateProfileClick(@PathVariable("profileID") String profileID) {
        suspendController.reactivateProfile(profileID);
        showSuccessMessage("Profile reactivated successfully!");
        return ResponseEntity.ok("Success");
    }

    // ==========================================
    // Unified UI Rendering and Feedback Methods
    // ==========================================
    
    public void showSuccessMessage(String msg) { 
        System.out.println("[UI RENDER - SUCCESS] " + msg); 
    }
    
    public void showErrorMessage(String msg) { 
        System.out.println("[UI RENDER - ERROR] " + msg); 
    }
    
    public void renderTable(List<UserProfile> profiles) { 
        System.out.println("[UI RENDER - TABLE] Refreshing data table with " + profiles.size() + " records..."); 
    }
    
    public void openProfileDrawer(String roleName, String status) { 
        System.out.println("[UI RENDER - DRAWER] Opening side drawer -> Role: " + roleName + ", Status: " + status); 
    }
}
