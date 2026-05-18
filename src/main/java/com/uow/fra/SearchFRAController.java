package com.uow.fra;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class SearchFRAController {
    
    public List<FRA> searchFRA(String criteria, String categoryId, String status, String role, String userId, String startDate, String endDate) {
        
        if (role == null || userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String safeCriteria = (criteria != null && !criteria.isBlank()) ? criteria.trim() : null;
        String safeCategoryId = (categoryId != null && !categoryId.isBlank() && !"all".equalsIgnoreCase(categoryId)) ? categoryId.trim() : null;
        String safeStatus = (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status)) ? status.trim() : null;
        String safeStartDate = (startDate != null && !startDate.isBlank()) ? startDate.trim() : null;
        String safeEndDate = (endDate != null && !endDate.isBlank()) ? endDate.trim() : null;

        // Gracefully handle inverted date ranges (e.g., From 2026 > To 2025). 
        // Return an empty dataset instead of throwing an exception to prevent crashes.
        if (safeStartDate != null && safeEndDate != null) {
            if (safeStartDate.compareTo(safeEndDate) > 0) {
                return new ArrayList<>();
            }
        }

        return FRA.findFRAsByCriteria(safeCriteria, safeCategoryId, safeStatus, role, userId, safeStartDate, safeEndDate);
    }
}