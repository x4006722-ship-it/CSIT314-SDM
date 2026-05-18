package com.uow.fra;
import org.springframework.stereotype.Service;

/**
 * Handles creation of new Fund Raising Activities (FRAs) with validation.
 * 
 * Responsibilities:
 * - Validate FRA data (title, amount, dates, people assignments)
 * - Ensure dates are logically ordered (start before end)
 * - Check for duplicate FRA titles
 * - Delegate to FRA entity for database persistence
 */
@Service
public class CreateFRAController {
    
    /**
     * Creates a new FRA with comprehensive validation.
     * 
     * Validates:
     * - All required fields are present and not empty
     * - Target amount is greater than 0
     * - Start date is before end date
     * - FRA title is not a duplicate
     * 
     * @param fraData The FRA object with creation details
     * @return The created FRA with generated ID, or null if validation fails
     */
    public FRA createFRA(FRA fraData) {
        // 1. boundary validation 
        if (fraData == null ||
            fraData.getFraTitle() == null || fraData.getFraTitle().trim().isEmpty() ||
            fraData.getFraTargetAmount() == null || fraData.getFraTargetAmount() <= 0 ||
            fraData.getCategoryId() == null || fraData.getCategoryId().trim().isEmpty() ||
            fraData.getDoneeId() == null || fraData.getDoneeId().trim().isEmpty() ||
            fraData.getFundRaiserId() == null || fraData.getFundRaiserId().trim().isEmpty() ||
            fraData.getStartedAt() == null || fraData.getStartedAt().trim().isEmpty() ||
            fraData.getEndedAt() == null || fraData.getEndedAt().trim().isEmpty()) {
            return null; 
        }

        // 2. date logic
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return null; 
        }

        // 3. duplicate check
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), null)) {
            return null; 
        }

        // 4. execute save
        return fraData.saveFRA();
    }
}
