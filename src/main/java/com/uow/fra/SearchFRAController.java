package com.uow.fra;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class SearchFRAController {
    public List<FRA> searchFRA(String criteria, String categoryId, String status, String role, String userId, String startDate) {
        // 【核心修复】：除了 role 和 userId 必须要有（防止越权），其他搜索条件（如关键词、日期）都是可选的，允许为 null！
        if (role == null || userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return FRA.findFRAsByCriteria(criteria, categoryId, status, role, userId, startDate);
    }
}