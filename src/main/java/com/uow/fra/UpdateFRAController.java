package com.uow.fra;

import org.springframework.stereotype.Service;

@Service
public class UpdateFRAController {

    public boolean updateFRA(String fraId, FRA fraData) {
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return false;
        }
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), fraId)) {
            return false;
        }
        fraData.setFraId(fraId);
        return fraData.updateFRAData();
    }
}
