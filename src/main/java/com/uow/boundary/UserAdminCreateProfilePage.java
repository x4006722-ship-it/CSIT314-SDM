package com.uow.boundary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uow.control.UserAdminCreateProfileController;
import com.uow.entity.CreateUserProfile;
import com.uow.entity.ViewUserProfile; // 删除了重复的 import，也删除了 DTO

@RestController
@RequestMapping("/api/profiles")
/**
 * Boundary (API) for listing and creating user profiles (roles).
 */
public class UserAdminCreateProfilePage {

    private final UserAdminCreateProfileController controller;

    @Autowired
    public UserAdminCreateProfilePage(UserAdminCreateProfileController controller) {
        this.controller = controller;
    }

    // 🌟 修复点 1：返回 List<ViewUserProfile>，而不是 ProfileDTO
    // 🌟 修复点 2：严禁跨级调用 Entity，直接向 Controller 要数据
    @GetMapping("/list")
    public List<ViewUserProfile> listProfiles() {
        System.out.println("[BOUNDARY] 收到获取全部 Profile 列表的请求");
        return controller.getAllProfiles(); 
    }

    @PostMapping("/create")
    public String submitForm(
            @RequestParam("roleName") String roleName,
            @RequestParam("status") String status) {

        System.out.println("[BOUNDARY] 收到创建请求: " + roleName);
        System.out.println("参数 - roleName: " + roleName + ", status: " + status);

        try {
            String err = controller.createProfile(roleName, status);

            if (err == null) {
                System.out.println("[BOUNDARY] Profile 创建成功");
                return "Success: Profile '" + roleName + "' has been saved!";
            }
            if (CreateUserProfile.MSG_ROLE_ALREADY_EXISTS.equals(err)) {
                return CreateUserProfile.MSG_ROLE_ALREADY_EXISTS;
            }
            System.err.println("[BOUNDARY] Profile 创建失败: " + err);
            return "Error: " + err;
        } catch (Exception e) {
            System.err.println("[BOUNDARY] 创建过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
