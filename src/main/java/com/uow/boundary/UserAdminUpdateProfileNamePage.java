package com.uow.boundary;

import com.uow.control.UserAdminUpdateProfileNameController;
import com.uow.entity.CreateUserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 更新用户资料名称的 Boundary 层（REST API）
 * 责任：接收 HTTP 请求，调用 Controller，返回 JSON 响应
 */
@RestController
@RequestMapping("/api/profiles")
public class UserAdminUpdateProfileNamePage {

    private final UserAdminUpdateProfileNameController controller;

    @Autowired
    public UserAdminUpdateProfileNamePage(UserAdminUpdateProfileNameController controller) {
        this.controller = controller;
    }

    /**
     * 更新 profile 的 role 名称
     * @param profileId 要更新的 profile ID
     * @param newRoleName 新的 role 名称
     * @return 操作结果
     */
    @PostMapping("/update-role/{profileId}")
    public ResponseEntity<String> updateRoleName(
            @PathVariable("profileId") String profileId,
            @RequestParam("newRoleName") String newRoleName) {
        try {
            System.out.println("[BOUNDARY] Received update role name request for profile: " + profileId + ", new name: " + newRoleName);

            String err = controller.updateRoleName(profileId, newRoleName);

            if (err == null) {
                return ResponseEntity.ok("Role name updated successfully");
            }
            if (CreateUserProfile.MSG_ROLE_ALREADY_EXISTS.equals(err)) {
                return ResponseEntity.badRequest().body(CreateUserProfile.MSG_ROLE_ALREADY_EXISTS);
            }
            return ResponseEntity.badRequest().body("Error: " + err);
        } catch (Throwable t) {
            System.err.println("[BOUNDARY] Unexpected error while updating role name: " + t.getMessage());
            t.printStackTrace();
            return ResponseEntity.internalServerError().body("Update failed: " + t.getClass().getSimpleName() + " - " + t.getMessage());
        }
    }
}
