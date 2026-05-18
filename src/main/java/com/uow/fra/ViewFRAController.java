package com.uow.fra;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class ViewFRAController {
    public List<FRA> viewAllFRAs(String fundRaiserId) {
        if (fundRaiserId == null || fundRaiserId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return FRA.findAllFRAs(fundRaiserId); 
    }
}