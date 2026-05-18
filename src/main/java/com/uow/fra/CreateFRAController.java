package com.uow.fra;

import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class CreateFRAController {
    
    public FRA createFRA(FRA fraData) {
        // 1. Basic Defense: Intercept all null values and empty strings to prevent 
        // NullPointerExceptions or database insertion failures downstream.
        if (fraData == null || 
            isBlank(fraData.getFraTitle()) || 
            isBlank(fraData.getCategoryId()) || 
            isBlank(fraData.getDoneeId()) || 
            isBlank(fraData.getFundRaiserId()) || 
            isBlank(fraData.getStartedAt()) || 
            isBlank(fraData.getEndedAt())) {
            return null;
        }

        // 2. Amount Boundary Check: Prevent negative numbers, zero, or astronomical 
        // numbers that exceed the DOUBLE range from crashing the database.
        if (fraData.getFraTargetAmount() == null || 
            fraData.getFraTargetAmount() <= 0 || 
            fraData.getFraTargetAmount() > 1000000000.0) {
            return null;
        }

        // 3. Chronological Logic: Start date must be strictly before the end date.
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return null; 
        }

        // 4. Creation Timeliness: For newly initiated campaigns, the start date 
        // must not be earlier than today's date.
        try {
            LocalDate startDate = LocalDate.parse(fraData.getStartedAt().substring(0, 10));
            if (startDate.isBefore(LocalDate.now())) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        
        // 5. Business Uniqueness: Check the database to ensure the title doesn't already exist.
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), null)) {
            return null; 
        }

        return fraData.saveFRA();
    }

    // Helper method to check for null or empty strings safely
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}