package com.uow.boundary;

import com.uow.control.UserAdminSuspendProfileController;
import com.uow.control.UserAdminSuspendProfileController.SuspendProfileOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 挂起用户资料的 Boundary 层（REST API）
 * 责任：接收 HTTP 请求，调用 Controller，返回 JSON 响应
 */
@RestController
@RequestMapping("/api/profiles")
public class UserAdminSuspendProfilePage {

    private final UserAdminSuspendProfileController controller;

    @Autowired
    public UserAdminSuspendProfilePage(UserAdminSuspendProfileController controller) {
        this.controller = controller;
    }

    /**
     * 挂起（暂停）一个 profile
     * @param profileId 要挂起的 profile ID
     * @return 操作结果
     */
    @PostMapping("/suspend/{profileId}")
    public ResponseEntity<String> suspendProfile(@PathVariable("profileId") String profileId) {
        System.out.println("[BOUNDARY] Received suspend request for profile: " + profileId);

        SuspendProfileOutcome outcome = controller.suspendProfileWithOutcome(profileId);

        if (outcome == SuspendProfileOutcome.SUCCESS) {
            return ResponseEntity.ok("Profile suspended successfully");
        }
        if (outcome == SuspendProfileOutcome.USER_ADMIN_FORBIDDEN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User Admin profiles cannot be suspended.");
        }
        return ResponseEntity.badRequest().body("Failed to suspend profile");
    }

    /**
     * 重新激活一个 profile
     * @param profileId 要激活的 profile ID
     * @return 操作结果
     */
    @PostMapping("/reactivate/{profileId}")
    public ResponseEntity<String> reactivateProfile(@PathVariable("profileId") String profileId) {
        System.out.println("[BOUNDARY] Received reactivate request for profile: " + profileId);
        
        boolean success = controller.reactivateProfile(profileId);
        
        if (success) {
            return ResponseEntity.ok("Profile reactivated successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to reactivate profile");
        }
    }
}
