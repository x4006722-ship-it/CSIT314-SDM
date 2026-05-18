package com.uow.fra;

import org.springframework.stereotype.Service;

@Service
public class UpdateFRAController {
    
    public boolean updateFRA(String fraId, FRA fraData) {
        // 1. Basic parameter null and empty string checks
        if (fraData == null || fraId == null || fraId.trim().isEmpty() ||
            fraData.getFraTitle() == null || fraData.getFraTitle().trim().isEmpty() ||
            fraData.getStartedAt() == null || fraData.getStartedAt().trim().isEmpty() ||
            fraData.getEndedAt() == null || fraData.getEndedAt().trim().isEmpty()) {
            return false;
        }

        // 2. Target amount boundary and safety interception
        if (fraData.getFraTargetAmount() == null || 
            fraData.getFraTargetAmount() <= 0 || 
            fraData.getFraTargetAmount() > 1000000000.0) {
            return false;
        }

        // 3. Chronological validation (Only ensures start is before end. 
        // Past dates are allowed here so ongoing campaigns can still be updated).
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return false;
        }

        // 4. Duplicate Check (Excluding Self): Ensure the updated title doesn't 
        // collide with another existing campaign.
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), fraId)) {
            return false;
        }

        fraData.setFraId(fraId);
        return fraData.updateFRAData();
    }
}