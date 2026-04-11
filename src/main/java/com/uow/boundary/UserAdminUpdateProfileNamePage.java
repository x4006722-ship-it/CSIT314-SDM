package com.uow.boundary;

import com.uow.control.UserAdminUpdateProfileNameController;
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

    @PostMapping("/update-role/{profileId}")
    public ResponseEntity<String> updateRoleName(
            @PathVariable("profileId") String profileId,
            @RequestParam("newRoleName") String newRoleName) {
        try {
            System.out.println("[BOUNDARY] 收到更新 profile 名称请求: " + profileId + " -> " + newRoleName);

            String err = controller.updateRoleName(profileId, newRoleName);

            if (err == null) {
                return ResponseEntity.ok("Role name updated successfully");
            }
            if (UserAdminUpdateProfileNameController.MSG_ROLE_ALREADY_EXISTS.equals(err)) {
                return ResponseEntity.badRequest().body(UserAdminUpdateProfileNameController.MSG_ROLE_ALREADY_EXISTS);
            }
            return ResponseEntity.badRequest().body("Error: " + err);
            
        } catch (Throwable t) {
            System.err.println("[BOUNDARY] 发生意外错误: " + t.getMessage());
            t.printStackTrace();
            return ResponseEntity.internalServerError().body("Update failed: " + t.getMessage());
        }
    }
}
