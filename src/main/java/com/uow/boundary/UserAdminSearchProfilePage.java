package com.uow.boundary;

import com.uow.control.UserAdminSearchProfileController;
import com.uow.entity.ViewUserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 搜索用户资料的 Boundary 层（REST API）
 * 责任：接收 HTTP 请求，调用 Controller，返回 JSON 响应
 */
@RestController
@RequestMapping("/api/profiles")
public class UserAdminSearchProfilePage {

    private final UserAdminSearchProfileController controller;

    @Autowired
    public UserAdminSearchProfilePage(UserAdminSearchProfileController controller) {
        this.controller = controller;
    }

    /**
     * 搜索用户资料 API 端点
     * @param keyword 搜索关键词（可选）
     * @param status 状态过滤（可选）
     * @return ProfileDTO 列表（JSON 格式）
     */
    @GetMapping("/search")
    public ResponseEntity<List<ViewUserProfile.ProfileDTO>> searchProfiles(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {
            
        System.out.println("[BOUNDARY] Received search request - Keyword: " + keyword + ", Status: " + status);
        
        // 调用 Controller 执行搜索
        List<ViewUserProfile.ProfileDTO> results = controller.executeSearch(keyword, status);
        
        System.out.println("[BOUNDARY] Returning " + results.size() + " profiles to client");
        return ResponseEntity.ok(results);
    }
}