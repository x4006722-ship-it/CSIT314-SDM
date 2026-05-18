package com.uow.fra;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

/**
 * Handles searching for Fund Raising Activities.
 * 
 * Responsibilities (Logic layer):
 * - Clean and sanitize input parameters
 * - Validate user identity and permissions
 * - Perform date logic validation (e.g. start date cannot be after end date)
 * - Route clean data to Entity for querying
 */
@Service
public class SearchFRAController {
    
    /**
     * @param criteria Title search keyword
     * @param categoryId Category filter
     * @param status Status filter 
     * @param role User's role
     * @param userId User's ID
     * @param startDate Filter: Campaign must start ON or AFTER this date
     * @param endDate Filter: Campaign must end ON or BEFORE this date
     */
    public List<FRA> searchFRA(String criteria, String categoryId, String status, String role, String userId, String startDate, String endDate) {
        
        // ==========================================
        // 1. Controller层拦截: 基础权限与身份校验
        // ==========================================
        if (role == null || userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // ==========================================
        // 2. Controller层拦截: 数据清洗预处理 (避免空字符串污染SQL)
        // ==========================================
        String safeCriteria = (criteria != null && !criteria.isBlank()) ? criteria.trim() : null;
        String safeCategoryId = (categoryId != null && !categoryId.isBlank() && !"all".equalsIgnoreCase(categoryId)) ? categoryId.trim() : null;
        String safeStatus = (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status)) ? status.trim() : null;
        String safeStartDate = (startDate != null && !startDate.isBlank()) ? startDate.trim() : null;
        String safeEndDate = (endDate != null && !endDate.isBlank()) ? endDate.trim() : null;

        // ==========================================
        // 3. Controller层拦截: 日期业务逻辑校验
        // ==========================================
        if (safeStartDate != null && safeEndDate != null) {
            // 如果用户输入的"开始时间"晚于"结束时间"，这在逻辑上是不成立的无效区间
            // 直接在 Controller 层拦截，不再消耗数据库性能
            if (safeStartDate.compareTo(safeEndDate) > 0) {
                return new ArrayList<>();
            }
        }

        // ==========================================
        // 4. 委派执行: 将完全干净、合规的数据交给底层去查库
        // ==========================================
        return FRA.findFRAsByCriteria(safeCriteria, safeCategoryId, safeStatus, role, userId, safeStartDate, safeEndDate);
    }
}