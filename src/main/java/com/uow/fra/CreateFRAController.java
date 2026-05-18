package com.uow.fra;

import org.springframework.stereotype.Service;

@Service
public class CreateFRAController {

    public FRA createFRA(FRA fraData) {
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return null;
        }
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), null)) {
            return null;
        }
        return fraData.saveFRA();
    }
}
