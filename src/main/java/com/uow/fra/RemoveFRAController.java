package com.uow.fra;
import org.springframework.stereotype.Service;

/**
 * Handles deletion/removal of Fund Raising Activities.
 * 
 * Responsibilities:
 * - Validate FRA ID
 * - Delegate to FRA entity for database deletion
 */
@Service
public class RemoveFRAController {
    /**
     * Deletes an FRA from the system.
     * 
     * @param fraId The FRA ID to delete
     * @return true if deletion was successful, false if validation fails
     */
    public boolean deleteFRA(String fraId) {
        // 边界检查
        if (fraId == null || fraId.trim().isEmpty()) {
            return false;
        }
        FRA target = new FRA();
        target.setFraId(fraId);
        return target.removeFRA();
    }
}
