package com.uow.userprofile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserProfilePage {

    @Autowired
    private CreateUserProfileController createUserProfileController;

    @Autowired
    private ViewUserProfileController viewUserProfileController;

    @Autowired
    private UpdateUserProfileController updateUserProfileController;

    @Autowired
    private SuspendUserProfileController suspendUserProfileController;

    @Autowired
    private SearchUserProfileController searchUserProfileController;

    @GetMapping("/manage-profile")
    public String showProfilePage() {
        return "forward:/ManageProfile.html";
    }

    public String showProfileSuccessMessage(String message) {
        String msg = message == null ? "" : message;
        return "redirect:/ManageProfile.html?toast=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
    }

    public String showProfileErrorMessage(String message) {
        String msg = message == null || message.isBlank() ? "" : message;
        return "redirect:/ManageProfile.html?toast=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
    }

    // Create Profile
    @PostMapping(path = "/api/profiles/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onCreateProfile(@RequestParam(value = "roleName", defaultValue = "") String roleName,
                                  @RequestParam(value = "status", defaultValue = "") String status) {
        boolean ok = createUserProfileController.createProfile(roleName, status);
        return ok
                ? showProfileSuccessMessage("Profile created successfully.")
                : showProfileErrorMessage(createUserProfileController.getErrorMessage());
    }

    // View Profile
    @GetMapping("/api/profiles/view")
    @ResponseBody
    public Object onViewProfile(@RequestParam(value = "profileID", defaultValue = "0") int profileID) {
        return viewUserProfileController.viewProfile(profileID);
    }

    // Update Profile
    @PostMapping(path = "/api/profiles/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onUpdateProfile(@RequestParam(value = "profileID", defaultValue = "0") int profileID,
                                  @RequestParam(value = "roleName", defaultValue = "") String roleName,
                                  @RequestParam(value = "status", defaultValue = "") String status) {
        boolean ok = updateUserProfileController.updateProfile(profileID, roleName, status);
        return ok
                ? java.util.Map.of("success", true, "message", "Profile updated successfully.")
                : java.util.Map.of("success", false, "error", updateUserProfileController.getErrorMessage());
    }

    // Suspend Profile (toggle Active ↔ Suspended)
    @PostMapping(path = "/api/profiles/suspend", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSuspendProfile(@RequestParam(value = "profileID", defaultValue = "0") int profileID) {
        boolean ok = suspendUserProfileController.suspendProfile(profileID);
        return ok
                ? java.util.Map.of("success", true, "message", "Profile status updated successfully.")
                : java.util.Map.of("success", false, "error", suspendUserProfileController.getErrorMessage());
    }

    // Search Profile
    @GetMapping({"/api/profiles/search", "/api/profiles/list"})
    @ResponseBody
    public Object onSearchProfile(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                  @RequestParam(value = "status", defaultValue = "") String status) {
        Object result = searchUserProfileController.searchProfile(keyword, status);
        return result == null ? java.util.List.of() : result;
    }
}
