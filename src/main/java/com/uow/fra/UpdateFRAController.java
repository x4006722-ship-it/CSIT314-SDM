package com.uow.fra;
import org.springframework.stereotype.Service;

/**
 * Handles updates to existing Fund Raising Activities with validation.
 * 
 * Responsibilities:
 * - Validate FRA data (same as Create)
 * - Ensure date logic is correct
 * - Check for duplicate titles (excluding the current FRA)
 * - Persist changes to database
 */
@Service
public class UpdateFRAController {
    
    /**
     * Updates an existing FRA with validation.
     * 
     * Validates:
     * - FRA ID and data are valid
     * - All required fields are present
     * - Target amount is greater than 0
     * - Start date is before end date
     * - No duplicate title (excluding this FRA)
     * 
     * @param fraId The FRA ID to update
     * @param fraData The updated FRA information
     * @return true if update was successful, false if validation fails
     */
    public boolean updateFRA(String fraId, FRA fraData) {
        // 1. 和 Create 保持同等强度的边界防御
        if (fraData == null || fraId == null || fraId.trim().isEmpty() ||
            fraData.getFraTitle() == null || fraData.getFraTitle().trim().isEmpty() ||
            fraData.getFraTargetAmount() == null || fraData.getFraTargetAmount() <= 0 ||
            fraData.getStartedAt() == null || fraData.getStartedAt().trim().isEmpty() ||
            fraData.getEndedAt() == null || fraData.getEndedAt().trim().isEmpty()) {
            return false;
        }

        // 2. 日期逻辑
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return false;
        }

        // 3. 高性能业务查重 (排除自身)
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), fraId)) {
            return false;
        }

        fraData.setFraId(fraId);
        return fraData.updateFRAData();
    }
}