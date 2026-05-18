package com.uow.donee;

import com.uow.fra.FRA;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Handles searching for donations/fundraising progress.
 * 
 * Responsibilities:
 * - Validate user identity and permissions
 * - Enforce business rules (search field length, date logic)
 * - Search for FRAs (donations) by title and date range
 * - Return proper error messages for validation failures
 */
@Component
public class SearchDonationController {

    /**
     * Searches for FRAs (donations/fundraising activities) with business rule validation.
     * 
     * Validates:
     * - User identity is provided
     * - Search title is not too long (max 50 characters)
     * - Date range is logical (start <= end)
     * 
     * @param searchDonationData A Map with userId, title (optional), startDate (optional), endDate (optional)
     * @return List of matching FRAs, or a Map with error message if validation fails
     */
    public Object searchDonation(Object searchDonationData) {
        // 1. 修复泛型类型：将 <?, ?> 改为 <String, Object>
        if (!(searchDonationData instanceof Map)) {
            return List.of();
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) searchDonationData;

        // 2. 提取参数
        String title = String.valueOf(data.getOrDefault("title", "")).trim();
        String startDateStr = String.valueOf(data.getOrDefault("startDate", ""));
        String endDateStr = String.valueOf(data.getOrDefault("endDate", ""));
        Object userIdObj = data.get("userId");

        // ================= 【业务规则 1：权限规则】 =================
        if (userIdObj == null || String.valueOf(userIdObj).equals("0")) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Access Denied: User identity is missing.");
            return err;
        }

        // ================= 【业务规则 2：输入约束规则】 =================
        if (title.length() > 50) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Search criteria too long: Maximum 50 characters.");
            return err;
        }

        // ================= 【业务规则 3：日期逻辑规则】 =================
        if (startDateStr != null && !startDateStr.isBlank() && endDateStr != null && !endDateStr.isBlank()) {
            try {
                LocalDate start = LocalDate.parse(startDateStr);
                LocalDate end = LocalDate.parse(endDateStr);

                if (start.isAfter(end)) {
                    Map<String, String> err = new HashMap<>();
                    err.put("error", "Date From cannot be later than Date To.");
                    return err;
                }
            } catch (Exception e) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Invalid date format. Please use YYYY-MM-DD.");
                return err;
            }
        }

        // 3. 校验通过，调用 Entity
        return FRA.getSearchDonation(searchDonationData);
    }
}
