package com.uow.fra;

import org.springframework.stereotype.Service;

@Service
public class RemoveFRAController {

    public boolean deleteFRA(String fraId) {
        FRA target = new FRA();
        target.setFraId(fraId);
        return target.removeFRA();
    }
}
