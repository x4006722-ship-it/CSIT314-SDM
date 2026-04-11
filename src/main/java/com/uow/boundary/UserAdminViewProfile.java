package com.uow.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.uow.control.UserAdminViewController;

/**
 * Rest Controller，构成“查看用户资料”用例的边界层。
 * 它严格处理 REST 端点，并将 HTTP 请求映射到控制层的方法调用。
 */
@RestController
@RequestMapping("/api/profiles")
public class UserAdminViewProfile {

    private final UserAdminViewController controller;

    @Autowired
    public UserAdminViewProfile(UserAdminViewController controller) {
        this.controller = controller;
    }

    /**
     * 通过 ID 查看用户资料的 REST 端点。
     * @param profileId 从 URL 路径中获取的用户资料 ID。
     * @return 包含格式化后的用户资料信息的字符串。
     */
    @GetMapping("/view/{profileId}")
    @ResponseBody
    public String viewProfile(@PathVariable("profileId") String profileId) {
        System.out.println("[BOUNDARY] 边界层收到查看用户资料的请求: " + profileId);

        // 将请求传递给控制层
        String profileInfo = controller.getProfile(profileId);

        System.out.println("[BOUNDARY] 边界层返回资料显示");
        return profileInfo;
    }
}
