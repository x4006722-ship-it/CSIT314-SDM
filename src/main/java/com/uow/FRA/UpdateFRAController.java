package com.uow.FRA;

import org.springframework.stereotype.Service;


@Service
public class UpdateFRAController {
    public String updateFRA(String fraId, FRA fraData) {
        // Business logic: merging incoming data to the target entity
        // Note: Real world apps would findByID first, then update
        fraData.updateFRAData();
        return "Success";
    }
}
