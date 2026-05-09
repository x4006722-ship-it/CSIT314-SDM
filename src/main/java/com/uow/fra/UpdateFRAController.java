package com.uow.fra;

import org.springframework.stereotype.Service;


@Service
public class UpdateFRAController {
    public boolean updateFRA(String fraId, FRA fraData) {
        fraData.setFraId(fraId);
        return fraData.updateFRAData();
    }
}
