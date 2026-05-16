package com.uow.donee;

import com.uow.fra.FRA;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Component
public class SearchDonationController {

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
