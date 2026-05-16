package com.uow.fra;
import org.springframework.stereotype.Service;

@Service
public class CreateFRAController {
    
    public FRA createFRA(FRA fraData) {
        // 1. boundary validation 
        if (fraData == null ||
            fraData.getFraTitle() == null || fraData.getFraTitle().trim().isEmpty() ||
            fraData.getFraTargetAmount() == null || fraData.getFraTargetAmount() <= 0 ||
            fraData.getCategoryId() == null || fraData.getCategoryId().trim().isEmpty() ||
            fraData.getDoneeId() == null || fraData.getDoneeId().trim().isEmpty() ||
            fraData.getFundRaiserId() == null || fraData.getFundRaiserId().trim().isEmpty() ||
            fraData.getStartedAt() == null || fraData.getStartedAt().trim().isEmpty() ||
            fraData.getEndedAt() == null || fraData.getEndedAt().trim().isEmpty()) {
            return null; 
        }

        // 2. date logic
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return null; 
        }

        // 3. duplicate check
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), null)) {
            return null; 
        }

        // 4. execute save
        return fraData.saveFRA();
    }
}
