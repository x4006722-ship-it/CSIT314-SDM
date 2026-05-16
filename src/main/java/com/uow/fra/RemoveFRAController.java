package com.uow.fra;
import org.springframework.stereotype.Service;

@Service
public class RemoveFRAController {
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
