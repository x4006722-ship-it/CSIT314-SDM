package com.uow.fra;
import org.springframework.stereotype.Service;

@Service
public class UpdateFRAController {
    
    public boolean updateFRA(String fraId, FRA fraData) {
        // 1. Same boundary validation as Create
        if (fraData == null || fraId == null || fraId.trim().isEmpty() ||
            fraData.getFraTitle() == null || fraData.getFraTitle().trim().isEmpty() ||
            fraData.getFraTargetAmount() == null || fraData.getFraTargetAmount() <= 0 ||
            fraData.getStartedAt() == null || fraData.getStartedAt().trim().isEmpty() ||
            fraData.getEndedAt() == null || fraData.getEndedAt().trim().isEmpty()) {
            return false;
        }

        // 2. Date validation
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return false;
        }

        // 3. Duplicate title check (excluding this record)
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), fraId)) {
            return false;
        }

        fraData.setFraId(fraId);
        return fraData.updateFRAData();
    }
}