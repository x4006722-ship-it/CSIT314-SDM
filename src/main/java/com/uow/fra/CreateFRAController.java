package com.uow.fra;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CreateFRAController {
    
    public FRA createFRA(FRA fraData) {
        // Silent Boundary Protection
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

        // Business Logic 
        List<FRA> existingFRAs = FRA.findAllFRAs();
        for (FRA existing : existingFRAs) {
            if (existing.getFraTitle().equalsIgnoreCase(fraData.getFraTitle().trim())) {
                return null; 
            }
        }

        // 日期逻辑：确保开始时间早于结束时间
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return null; 
        }
        return fraData.saveFRA();
    }
}