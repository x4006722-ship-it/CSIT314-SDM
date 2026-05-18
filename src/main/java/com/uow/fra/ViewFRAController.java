package com.uow.fra;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

/**
 * Retrieves Fund Raising Activities for display.
 * 
 * Responsibilities:
 * - Query FRAs belonging to a specific Fund Raiser
 * - Validate Fund Raiser ID
 * - Return list of FRAs in order
 */
@Service
public class ViewFRAController {
    // 加上 fundRaiserId 参数，并传递给底层
    /**
     * Retrieves all FRAs created by a specific Fund Raiser.
     * 
     * @param fundRaiserId The Fund Raiser's user ID
     * @return A list of FRAs created by this Fund Raiser; empty list if none or invalid ID
     */
    public List<FRA> viewAllFRAs(String fundRaiserId) {
        // 边界验证
        if (fundRaiserId == null || fundRaiserId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return FRA.findAllFRAs(fundRaiserId); 
    }
}