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
        // 1. Cast raw Map to typed Map<String, Object>
        if (!(searchDonationData instanceof Map)) {
            return List.of();
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) searchDonationData;

        // 2. Extract parameters
        String title = String.valueOf(data.getOrDefault("title", "")).trim();
        String startDateStr = String.valueOf(data.getOrDefault("startDate", ""));
        String endDateStr = String.valueOf(data.getOrDefault("endDate", ""));
        Object userIdObj = data.get("userId");

        // Business rule 1: access control — userId must be present
        if (userIdObj == null || String.valueOf(userIdObj).equals("0")) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Access Denied: User identity is missing.");
            return err;
        }

        // Business rule 2: input constraint — title max 50 characters
        if (title.length() > 50) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Search criteria too long: Maximum 50 characters.");
            return err;
        }

        // Business rule 3: date logic — start must not be after end
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

        // 3. Validation passed — invoke Entity
        return FRA.getSearchDonation(searchDonationData);
    }
}
