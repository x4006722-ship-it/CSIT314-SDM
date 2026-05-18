package com.uow.donee;

import com.uow.fra.FRA;
import org.springframework.stereotype.Component;

/**
 * Retrieves detailed information about a specific donation/fundraising activity.
 * 
 * Responsibilities:
 * - Query FRA details by ID
 * - Return donation/fundraising progress information
 */
@Component
public class ViewDonationController {

    /**
     * Retrieves donation/FRA details for a specific fundraising activity.
     * 
     * @param fraId The FRA ID to retrieve details for
     * @return FRA details including progress, target, and donor information
     */
    public Object viewDonation(int fraId) {
        return FRA.getViewDonation(fraId);
    }
}
