package com.uow.fra;

import org.springframework.stereotype.Service;

@Service
public class RemoveFRAController {
    
    public boolean deleteFRA(String fraId) {
        // Validation: Cannot delete if ID is empty
        if (fraId == null || fraId.trim().isEmpty()) {
            return false;
        }
        
        FRA target = new FRA();
        target.setFraId(fraId);
        
        // Delegates the actual DB heavy lifting to the Entity
        return target.removeFRA();
    }
}
