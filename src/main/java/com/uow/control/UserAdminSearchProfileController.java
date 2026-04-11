package com.uow.control;

import com.uow.entity.SearchUserProfile;
import com.uow.entity.ViewUserProfile;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 搜索用户资料的 Control 层
 * 责任：协调 Entity 和 Boundary，执行业务逻辑
 */
@Service
public class UserAdminSearchProfileController {

    /**
     * 执行搜索操作
     * @param keyword 搜索关键词
     * @param status 状态过滤
     * @return ProfileDTO 列表
     */
    public List<ViewUserProfile> executeSearch(String keyword, String status) {
        System.out.println("[CONTROL] Searching profiles - Keyword: " + keyword + ", Status: " + status);

        // 调用 Entity 执行数据库查询
        List<ViewUserProfile> results = SearchUserProfile.searchDatabase(keyword, status);

        System.out.println("[CONTROL] Search returned " + results.size() + " results");
        return results;
    }
}
