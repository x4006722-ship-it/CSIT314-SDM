package com.uow.fra;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class ViewFRAController {
    // 加上 fundRaiserId 参数，并传递给底层
    public List<FRA> viewAllFRAs(String fundRaiserId) {
        // 边界验证
        if (fundRaiserId == null || fundRaiserId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return FRA.findAllFRAs(fundRaiserId); 
    }
}