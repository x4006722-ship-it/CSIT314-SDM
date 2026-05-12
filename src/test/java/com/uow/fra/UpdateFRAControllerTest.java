package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UpdateFRAControllerTest {

    private UpdateFRAController updateController;

    @Before
    public void setUp() {
        updateController = new UpdateFRAController();
    }

    @Test
    public void testUpdateFRA_NullData_ReturnsFalse() {
        // Test Rule 1: Silent Boundary Protection
        boolean result = updateController.updateFRA("123", null);
        assertFalse("Result should be false when the provided data is null", result);
    }

    @Test
    public void testUpdateFRA_StartDateAfterEndDate_ReturnsFalse() {
        // Test Rule 2: Date Logic Check
        FRA invalidDateFra = new FRA();
        invalidDateFra.setFraTitle("Valid Title");
        invalidDateFra.setStartedAt("2024-12-31"); 
        invalidDateFra.setEndedAt("2024-01-01"); // End date is before start date

        boolean result = updateController.updateFRA("123", invalidDateFra);
        assertFalse("Result should be false when the start date is after the end date", result);
    }

    @Test
    public void testUpdateFRA_SameStartAndEndDate_ReturnsFalse() {
        // Test Rule 2: Date Logic Check (Edge Case)
        FRA sameDateFra = new FRA();
        sameDateFra.setFraTitle("Valid Title");
        sameDateFra.setStartedAt("2024-05-01"); 
        sameDateFra.setEndedAt("2024-05-01"); // Exact same dates

        boolean result = updateController.updateFRA("123", sameDateFra);
        assertFalse("Result should be false when the start date equals the end date", result);
    }
}