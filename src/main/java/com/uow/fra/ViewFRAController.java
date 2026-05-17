package com.uow.fra;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class ViewFRAController {
    // Accepts fundRaiserId and passes it to the data layer
    public List<FRA> viewAllFRAs(String fundRaiserId) {
        // Boundary validation
        if (fundRaiserId == null || fundRaiserId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return FRA.findAllFRAs(fundRaiserId); 
    }
}